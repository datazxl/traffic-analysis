package com.zxl.web

import java.util

import com.zxl.parser.ParserLog
import com.zxl.parser.dataobject.{BaseDataObject, InvalidLogObject, ParsedDataObject}
import com.zxl.parser.dataobjectbuilder._
import com.zxl.preparser.PreParsedLog
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 和parser-weblog交互的类
  */
object WebParserLog {

  private val logger = LoggerFactory.getLogger(WebParserLog.getClass)

  // 构建解析PreParsedLog对象为DataObject的ParserLog
  val parser: ParserLog = {
    val cmds = new mutable.HashSet[String]()
    cmds.add("pv")
    cmds.add("mc")
    cmds.add("ev")
    cmds.add("hb")

    val builders = new mutable.HashSet[AbstractDataObjectBuilder]()
    builders.add(new PvDataObjectBuilder)
    builders.add(new McDataObjectBuilder)
    builders.add(new EvDataObjectBuilder)
    builders.add(new HbDataObjectBuilder)

    new ParserLog(cmds, builders)
  }

  def parse(preParsedLog: PreParsedLog): Seq[(CombinedId, BaseDataObject)] = {
    val parsedDataObjects: util.List[_ <: ParsedDataObject] = parser.parse(preParsedLog)

    val buffer = new ArrayBuffer[(CombinedId, BaseDataObject)]

    parsedDataObjects.foreach {
      case base: BaseDataObject => {
        val combinedId = CombinedId(base.getProfileId, base.getUserId)
        if (!AbnormalUserDetector.hasReachUserLimit(combinedId)){ //如果一个分区内某个用户的dataobject超过一定数量，过滤掉超过数量的dataobject
          buffer += ((combinedId, base))
        }
      }
      case invalid: InvalidLogObject => {
        logger.error(s"Invalid data object while parsing RequestInfo,\ndetails:${invalid}")
      }
    }

    buffer
  }
}