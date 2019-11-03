package com.zxl.web.userinfo

import com.zxl.web.CombinedId

/**
  * 读写用户历史访问行为数据
  */
trait UserVisitInfoComponent {
  /**
    * 根据访客唯一标识查询访客的历史访问信息
    * @param ids
    * @return
    */
  def findUserVisitInfo(ids: Seq[CombinedId]): Map[CombinedId, UserVisitInfo]

  /**
    * 更新用户的历史访问行为数据信息
    * @param users
    */
  def updateUserVisitInfo(users: Seq[UserVisitInfo]):Unit
}
