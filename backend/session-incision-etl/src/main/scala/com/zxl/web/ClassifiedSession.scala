package com.zxl.web

import com.zxl.parser.dataobject._
import com.zxl.parser.dataobject.dim.TargetPageInfo

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 给一个会话中的所以dataObject进行归类(会话级别)
  *
  * @param sessionIndex
  * @param session
  */
case class ClassifiedSession(sessionIndex: Int, session: Seq[BaseDataObject]) {

  val pvs = ArrayBuffer.empty[PvDataObject]
  val mcs = ArrayBuffer.empty[McDataObject]
  val hbMap = mutable.Map.empty[String, HbDataObject]
  val evs = ArrayBuffer.empty[EvDataObject]
  val targetPages = ArrayBuffer.empty[TargetPageDataObject]

  session.foreach(dataObj => {
    dataObj match {
      case pv: PvDataObject => pvs += pv
      case mc: McDataObject => mcs += mc
      case hb: HbDataObject => hbMap += hb.getPvId -> hb
      case ev: EvDataObject => evs += ev
      case tg: TargetPageDataObject => targetPages += tg
    }
  })


  //这个会话中的第一个pv的发生的时间就是会话开始的时间
  //如果这个会话中没有pv的话，那么就取第一个dataObject发生的时间
  val sessionStartTime = (pvs.headOption getOrElse session.head).getServerTime.getTime

  //当前会话选中的pv，即profileId大于0且是重要入口的pv
  def paidPVOpt: Option[PvDataObject] = pvs.find(pv => pv.isPaid && pv.getProfileId > 0)
  //第一个dataObject
  //先选择重要入口的pv，没有的话则取第一个dataObject
  def firstDataObj: BaseDataObject = paidPVOpt.getOrElse(session.head)

  /**
    * 计算会话时长
    * 会话时长等于这个会话中所有的pv的停留时间
    *
    * @return 会话时长
    */
  def fetchSessionDuration: Int = {
    if (session.isEmpty) 0
    else {
      //1.设置每个pv的停留时长
      setPVDuration(pvs.toList)
      //2.返回所有的pv的停留时间之和
      pvs.map(_.getDuration).sum
    }
  }

  /**
    * 当前会话中所有的有效的目标页面
    *
    * @return
    */
  val allActiveTargetInfo: Seq[(PvDataObject, TargetPageInfo)] = {
    import scala.collection.JavaConversions.asScalaBuffer
    targetPages.flatMap(target => {
      target.getTargetPagesInfos.filter(_.isActive).map(targetInfo => (target.getPvDataObject, targetInfo))
    })
  }

  /**
    * 设置pv停留时长
    *
    * @param pvs 所有的pv
    * @return
    */
  private def setPVDuration(pvs: List[PvDataObject]): Any = {
    pvs match {
      //1. 当前的一个pv的停留时间等于后一个pv发生的时间减去当前pv发生的时间
      case first :: second :: rest => {
        val pvDuration = ((second.getServerTime.getTime - first.getServerTime.getTime) / 1000).toInt
        first.setDuration(pvDuration)
        //递归计算后续且非最后一个pv的停留时间
        setPVDuration(second :: rest)
      }

      //2. 最后一个pv的停留时间的计算
      case last :: Nil => {
        val currentPvHbOpt = hbMap.get(last.getPvId) //获取这个pv对应的hb
        val pvDuration = currentPvHbOpt match {
          //2.1 如果对应的hb存在的话则取hb中的页面停留时间
          case Some(hb) if (hb.getClientPageDuration != 0) => hb.getClientPageDuration
          case _ => //2.2
            //计算整个会话中的pv的平均停留时间
            val averagePvDuration = ((last.getServerTime.getTime - sessionStartTime) / Math.max(pvs.size - 1, 1)) / 1000
            //如果平均的pv的停留时间小于0的话，则为0，否则就取平均停留时间
            val defaultLastPvDuration = if (averagePvDuration < 0L) 0 else averagePvDuration.toInt
            //计算最后一个dataObject发生的时间和最后一个pv发生的时间的间隔
            val lastObjectTime = session.last.getServerTime.getTime
            val calculated = ((lastObjectTime - last.getServerTime.getTime) / 1000).toInt
            //如果calculated小于0则去平均停留时间，否则就取calculated
            if (calculated <= 0) defaultLastPvDuration else calculated
        }
        last.setDuration(pvDuration)
      }
    }
  }
}
