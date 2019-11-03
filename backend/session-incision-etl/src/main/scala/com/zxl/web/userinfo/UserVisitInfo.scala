package com.zxl.web.userinfo

import com.zxl.web.CombinedId

/**
  * 用户历史访问信息类
  * @param id 用户唯一标识
  * @param lastVisitTime 上次访问时间
  * @param lastVisitCount 上次访问次数
  */
case class UserVisitInfo(id: CombinedId,var lastVisitTime: Long = 0, var lastVisitCount: Int = 0)