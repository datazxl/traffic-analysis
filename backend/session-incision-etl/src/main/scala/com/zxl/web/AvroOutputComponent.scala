package com.zxl.web

import org.apache.avro.Schema
import org.apache.avro.file.{CodecFactory, DataFileWriter}
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection._

/**
  * ETL输出组件(以Avro的文件格式输出到HDFS)
  */
object AvroOutputComponent {
  // 一个分区中的所有task共用avroWriter
  private val avroWriters = mutable.HashMap.empty[String, DataFileWriter[GenericRecord]]

  /**
    * 将ETL计算出来的用户所有实体以Avro文件格式输出到HDFS中
    * @param dataRecords    已经计算好的实体
    * @param outputBasePath 输出位置信息
    * @param dateStr        处理的数据的日期
    * @param partitionIndex 第几个分区(分区的Index)
    */
  def writeDataRecords(dataRecords: Seq[DataRecords],
                       outputBasePath: String, dateStr: String, partitionIndex: Int) = {
    //1.将用户所有会话的所有的实体按照实体类名进行归类
    val recordsMap: Map[String, Seq[GenericRecord]] = toRecordsMap(dataRecords)

    //2.写每种实体类名对应的所有实体到HDFS中
    recordsMap.foreach { case (className, records) =>
      if (records.nonEmpty) {
        //根据实体类名拿到avroWriter，没有的话则创建对应的avroWriter
        val writer = avroWriters.getOrElseUpdate(className,
          createAvroWriter(className, outputBasePath, dateStr, partitionIndex, records(0).getSchema))

        //将数据写出
        records.foreach(writer.append(_))
      }
    }
  }

  /**
    * 初始化一个avro对象的writer
    * @param objectType     实体类名(即实体类型)
    * @param outputBaseDir  输出路径
    * @param dateStr        数据的日期
    * @param partitionIndex 分区index
    * @param schema         实体对应的schema
    * @return AvroWriter
    */
  private def createAvroWriter(objectType: String, outputBaseDir: String,
                               dateStr: String, partitionIndex: Int, schema: Schema): DataFileWriter[GenericRecord] = {

    val (year, month, day) = (dateStr.take(4), dateStr.take(6), dateStr)
    val pathString = s"${outputBaseDir}/${objectType.toLowerCase}/year=$year/month=$month/day=$day/part-r-${partitionIndex}.avro"
    val path = new Path(pathString)
    val conf = new Configuration()
    val fs = FileSystem.get(conf)
    if (fs.exists(path)) {
      fs.delete(path, false)
    }
    val outputStream = fs.create(path)
    val writer = new DataFileWriter[GenericRecord](new SpecificDatumWriter[GenericRecord]()).setSyncInterval(100) //每批次100bit数据量
    writer.setCodec(CodecFactory.snappyCodec())
    writer.create(schema, outputStream)

    writer
  }

  /**
    * 将用户所有会话的所有的实体按照实体类名进行归类
    *
    * @param dataRecords 需要分类的所有的实体
    * @return 返回Map(实体类名 -> 对应的所有的实体列表)
    */
  private def toRecordsMap(dataRecords: Seq[DataRecords]): Map[String, Seq[GenericRecord]] = {
    val sessions = dataRecords.map(_.session)
    val pageViews = dataRecords.flatMap(_.pageViews)
    val heartbeats = dataRecords.flatMap(_.hearBeats)
    val mouseClicks = dataRecords.flatMap(_.mouseClicks)
    val conversions = dataRecords.flatMap(_.conversions)

    immutable.HashMap(
      classOf[Session].getSimpleName -> sessions,
      classOf[Conversion].getSimpleName -> pageViews,
      classOf[Heartbeat].getSimpleName -> heartbeats,
      classOf[MouseClick].getSimpleName -> mouseClicks,
      classOf[PageView].getSimpleName -> conversions
    )
  }
}