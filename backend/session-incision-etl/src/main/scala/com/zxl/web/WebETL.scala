package com.zxl.web

import java.util
import com.zxl.parser.dataobject.BaseDataObject
import com.zxl.preparser.PreParsedLog
import com.zxl.web.userinfo.{HBaseConnectionFactory, HBaseUserVisitInfoComponent, UserVisitInfo}
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Admin, Connection}
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{Encoders, Row, SparkSession}
import org.apache.spark.{HashPartitioner, SparkConf}
import scala.collection.mutable.ArrayBuffer

/**
spark-submit --class com.zxl.web.WebETL \
--master spark://master:7077 \
--executor-cores 1 \
--executor-memory 1g \
--total-executor-cores 2 \
--conf spark.web.etl.inputBaseDir=hdfs://master:9000/user/hive/warehouse/rawdata.db/web \
--conf spark.web.etl.outputBaseDir=hdfs://master:9000/user/traffic-analysis/web \
--conf spark.web.etl.startDate=20180617 \
--conf spark.driver.extraJavaOptions="-Dweb.metadata.mongodbAddr=192.168.2.109 -Dweb.etl.hbase.zk.quorums=master,slave1,slave2" \
--conf spark.executor.extraJavaOptions="-Dweb.metadata.mongodbAddr=192.168.2.109 -Dweb.etl.hbase.zk.quorums=master,slave1,slave2 \
-Dcom.sun.management.jmxremote.port=1119 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false" \
/home/hadoop-zxl/course/traffic-analysis/session-incision-etl-1.0-SNAPSHOT-jar-with-dependencies.jar prod
  */
object WebETL {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","hadoop-zxl")

    //1.初始化SparkConf，创建SparkSession
    val conf = new SparkConf()
    if (args.isEmpty){
      conf.setMaster("local");
    }
    // 使用kryo序列化
    conf.set("spark.serializer", classOf[KryoSerializer].getName)
    conf.set("spark.kryo.registrator", classOf[WebRegistrator].getName)

    // 指定输入的目录
    val inputBasePath: String = conf.get("spark.web.etl.inputBaseDir",
      "hdfs://master:9000/user/hive/warehouse/rawdata.db/web")
    // 处理哪天日志数据
    val dateStr: String = conf.get("spark.web.etl.startDate", "20180616")
    val inputPath: String = s"${inputBasePath}/year=${dateStr.substring(0,4)}/month=${dateStr.substring(0,6)}/day=${dateStr}"
    // 指定输出的目录
    val outputBaseDir: String = conf.get("spark.web.etl.outputBaseDir",
      "hdfs://master:9000/user/traffic-analysis/web")

    // 指定程序名字
    conf.setAppName(s"WebETL-${dateStr}");
    // 指定shuffle默认的分区数
    val numPartitions = conf.getInt("spark.web.etl.shuffle.partitions", 5)

    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    //2.读取数据，转换为DataSet[PreParsedLog]，调用ParserLog，转换为各种DataObejct,构成RDD[CombinedId,DataObject]
    /**
      * WebLogParser.parse转换后数据为：
      * (CombinedId(profileId1,user1), BaseDataObject(profileId1,user1,pv,client_ip.....))
      * (CombinedId(profileId1,user1), BaseDataObject(profileId1,user1,mc,client_ip.....))
      * (CombinedId(profileId2,user2), BaseDataObject(profileId2,user2,pv,client_ip.....))
      * ............
      * (CombinedId(profileId3,user3), BaseDataObject(profileId3,user3,ev,client_ip.....))
      * (CombinedId(profileIdn,usern), BaseDataObject(profileIdn,usern,pv,client_ip.....))
      */
    val dataObjectsRDD: RDD[(CombinedId, BaseDataObject)] = spark.read.parquet(inputPath)
      .map(transform(_))(Encoders.bean(classOf[PreParsedLog])).rdd
      .flatMap(WebParserLog.parse(_))

    //3.将相同用户的访问行为数据聚合在一起
    /**
      * 转换后数据为：
      * (CombinedId(profileId1,user1), List(BaseDataObject(profileId1,user1,pv,client_ip.....),
      * BaseDataObject(profileId1,user1,mc,client_ip.....)))
      * (CombinedId(profileId2,user2), List(BaseDataObject(profileId2,user2,pv,client_ip.....),
      * BaseDataObject(profileId2,user2,mc,client_ip.....),
      * BaseDataObject(profileId2,user2,ev,client_ip.....)
      * BaseDataObject(profileId2,user2,hb,client_ip.....)))
      * ............
      * (CombinedId(profileId3,user3), List(BaseDataObject(profileId3,user3,ev,client_ip.....)))
      * (CombinedId(profileIdn,usern), List(BaseDataObject(profileIdn,usern,pv,client_ip.....),
      * BaseDataObject(profileIdn,usern,mc,client_ip.....),
      * BaseDataObject(profileIdn,usern,pv,client_ip.....)))
      */
    // 数据倾斜问题：一个访客一天内的访问行为数据不会超级多，如果一个用户数据量过多，那么聚合前就把它过滤掉
    val groupedDataObjects: RDD[(CombinedId, Iterable[BaseDataObject])] = dataObjectsRDD.groupByKey(new HashPartitioner(numPartitions))

    //4.进行会话切割，会话计算，写入最终实体表中
    groupedDataObjects.mapPartitionsWithIndex((index, iter) => {
      iter grouped(512) foreach (batchUsers => { // 每512个用户一个批次来操作

        // 获取该批次内所有用户的历史访问信息
        val visitInfosMap: Map[CombinedId, UserVisitInfo] = HBaseUserVisitInfoComponent.findUserVisitInfo(batchUsers.map(_._1))
        val updatedUserVisitInfos: ArrayBuffer[UserVisitInfo] = ArrayBuffer.empty[UserVisitInfo]

        // 遍历每个用户
        batchUsers.foreach{ case (combinedId, dataObjects) => {
          // 1.对该用户的所有dataobjects按照时间升序排列
          val sortedObjects: Seq[BaseDataObject] = dataObjects.toSeq.sortBy(_.getServerTime.getTime)
          // 2.会话切割
          val sessions: Seq[Seq[BaseDataObject]] = SessionIncision.getSessions(sortedObjects)
          // 3.会话计算
          val (visitInfo, records): (UserVisitInfo, Seq[DataRecords]) = SessionCompute.compute(combinedId, sessions, index + System.currentTimeMillis() * 1000, visitInfosMap.get(combinedId))
          updatedUserVisitInfos += visitInfo
          // 4.数据写出到HDFS
          AvroOutputComponent.writeDataRecords(records, outputBaseDir, dateStr, index)
        }}

        // 将该批次内的用户历史访问行为数据，保存到hbase
        HBaseUserVisitInfoComponent.updateUserVisitInfo(updatedUserVisitInfos)
      })

      Iterator[Unit]()
    }).foreach((_:Unit) => {})

    spark.stop()

    // 给hbase的web-user创建快照，方便后期重跑
    snapshot(dateStr)
  }

  private def transform(row: Row): PreParsedLog = {
    val p = new PreParsedLog
    p.setClientIp(row.getAs[String]("clientIp"))
    p.setCommand(row.getAs[String]("command"))
    p.setMethod(row.getAs[String]("method"))
    p.setProfileId(row.getAs[Int]("profileId"))
    p.setQueryString(row.getAs[String]("queryString"))
    p.setServerIp(row.getAs[String]("serverIp"))
    p.setServerPort(row.getAs[Int]("serverPort"))
    p.setServerTime(row.getAs[String]("serverTime"))
    p.setUriStem(row.getAs[String]("uriStem"))
    p.setUserAgent(row.getAs[String]("userAgent"))
    p
  }

  private def snapshot(dateStr: String): Unit ={
    //1. 获取连接
    val conn: Connection = HBaseConnectionFactory.getHbaseConn
    //2. 创建Admin，创建快照
    val admin: Admin = conn.getAdmin
    val tableName: String = System.getProperty("web.etl.hbase.UserTableName", "web-user")
    val snapshotName: String = s"${tableName}-${dateStr}"
    val snapshots: util.List[HBaseProtos.SnapshotDescription] = admin.listSnapshots(snapshotName)
    if (!snapshots.isEmpty){
      admin.deleteSnapshot(snapshotName)
    }
    admin.snapshot(snapshotName, TableName.valueOf(tableName))

    admin.close()
  }
}