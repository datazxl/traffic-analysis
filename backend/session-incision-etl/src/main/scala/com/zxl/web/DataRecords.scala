package com.zxl.web

/**
  * 封装一个用户的一个Session中所有最终访问实体数据，和最终表结构对应
  */
case class DataRecords(
                        session: Session,
                        pageViews: Seq[PageView],
                        mouseClicks: Seq[MouseClick],
                        hearBeats: Seq[Heartbeat],
                        conversions: Seq[Conversion]
                      )
