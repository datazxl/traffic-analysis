package com.zxl.preparser

import org.apache.spark.sql.{Dataset, Encoders, SaveMode, SparkSession}

/**
  * 对原始日志数据进行预解析，并入库到rawdata.web hive表中
  */
object PreParserETL {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .appName("PreParserETL")
      .master("local")
      .enableHiveSupport()
      .getOrCreate()

    //1. 读取原始数据，解析为PreParsedLog
    val inputPath: String = spark.conf.get("spark.traffic.analysis.rawdata.input", "hdfs://master:9000/traffic-analysis/rawlog/20180615")
    val numPartitions: Int = spark.conf.get("spark.traffic.analysis.rawdata.numPartitions", "2").toInt
    val preParsedLogDS: Dataset[PreParsedLog] = spark.read.textFile(inputPath).flatMap(line => Option(PreParserWebLog.parse(line)))(Encoders.bean(classOf[PreParsedLog]))

    //2. 将数据写入rawdata.web表中
    preParsedLogDS
      .repartition(numPartitions)
      .write
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day")
      .saveAsTable("rawdata.web");

    spark.stop()
  }
}
