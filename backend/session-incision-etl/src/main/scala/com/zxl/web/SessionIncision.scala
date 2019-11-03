package com.zxl.web

import com.zxl.parser.dataobject.{BaseDataObject, PvDataObject}

import scala.collection.mutable.ArrayBuffer

object SessionIncision {
  private val THIRTY_MINS_IN_MS = 30 * 60 * 1000;

  /**
    * 对一个user下的所有的访问记录(dataObjects)进行会话的切割，逻辑为：
    * 1、如果两个相邻的访问记录时间相隔超过30分钟，则切割为一个会话
    * 2、如果一个pv是重要的入口，则从这个pv开始重新算作一个新的会话
    *
    * @param sortedDataObjects 一个user的排序好的所有的dataObjects
    * @return 返回这个user产生的所有的会话，一个user可能产生若干个会话
    */
  def getSessions(sortedDataObjects: Seq[BaseDataObject]): Seq[Seq[BaseDataObject]] = {
    // 1.如果相邻的两个DataObject超过30，则进行会话切割
    val (sessions, session, _) = sortedDataObjects.foldLeft(ArrayBuffer.empty[ArrayBuffer[BaseDataObject]], ArrayBuffer.empty[BaseDataObject], null: BaseDataObject) {
      //sessions表示会话的列表，session表示一个会话中的BaseDataObject列表
      //preDataObj表示前一个BaseDataObject，curDataObj表示当前的BaseDataObject
      case ((sessions, session, preDataObj), curDataObj) => {
        //如果不是第一个BaseDataObject，则用当前的BaseDataObject和前一个BaseDataObject的时间进行比较
        //超过30分钟则切成一个新的会话
        if (preDataObj != null && curDataObj.getServerTime.getTime - preDataObj.getServerTime.getTime > THIRTY_MINS_IN_MS) {
          sessions += session.clone()
          session.clear()
          session += curDataObj
          (sessions, session, curDataObj)
        } else {
          //没有超过30分钟，则算在当前的会话中
          session += curDataObj
          (sessions, session, curDataObj)
        }
      }
    }
    //将最后的一个会话加入到会话列表sessions中
    sessions += session.clone()


    // 2.根据重要入口再次进行会话的切割
    val newSessions = sessions.flatMap(session => {

      val (newSessions, endSession, _) = session.foldLeft(ArrayBuffer.empty[ArrayBuffer[BaseDataObject]], ArrayBuffer.empty[BaseDataObject], null: PvDataObject) {
        case ((sessions, session, prePvDataObj), curDataObj) => {
          curDataObj match {
            // 如果是pv，并且是广告进来的，并且不是刷新来的，并且和前一个pv发送时间不同，那么就是重要入口，进行会话切割
            case curPvDataObj: PvDataObject if prePvDataObj != null && curPvDataObj != null
              && curPvDataObj.isPaid && curPvDataObj.isDifferentFrom(prePvDataObj)
              && curPvDataObj.getServerTime.getTime != prePvDataObj.getServerTime.getTime => {
              sessions += session.clone()
              session.clear()
              session += curPvDataObj
              (sessions, session, curPvDataObj)
            }
            case dataObj: BaseDataObject => {
              session += dataObj
              if (dataObj.getClass == classOf[PvDataObject]){
                (sessions, session, dataObj.asInstanceOf[PvDataObject])
              } else {
                (sessions, session, prePvDataObj)
              }
            }
          }
        }
      }

      //将最后的一个会话加入到会话列表sessions中
      newSessions += endSession.clone()
      newSessions
    })

    newSessions
  }
}