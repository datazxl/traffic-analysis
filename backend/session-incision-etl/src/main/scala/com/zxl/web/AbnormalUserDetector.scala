package com.zxl.web

import java.util.concurrent.ConcurrentHashMap

import org.slf4j.{Logger, LoggerFactory}

case class AbnormalUserDetector() {}

object AbnormalUserDetector {
  private val logger: Logger = LoggerFactory.getLogger(classOf[AbnormalUserDetector])
  //每个Executor允许某个用户的数据量最多为5000
  private val MAX_USERDATAOBJECT_PER_EXECUTOR = Integer.getInteger("wd.etl.MaxUserDataObjectPerExecutor", 5000)
  private val userCounter = new ConcurrentHashMap[CombinedId, Int]()

  def hasReachUserLimit(combinedId: CombinedId): Boolean = {
    val count: Int = userCounter.getOrDefault(combinedId, 0)
    if (count < MAX_USERDATAOBJECT_PER_EXECUTOR) {
      false
    } else {
      logger.warn(s"the user ${combinedId} total number of dataobjects exceded the limit ${MAX_USERDATAOBJECT_PER_EXECUTOR} in a executor")
      true
    }
  }
}