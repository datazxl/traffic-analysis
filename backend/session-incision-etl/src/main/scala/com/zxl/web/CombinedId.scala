package com.zxl.web

/**
  * 标识唯一的访客
  * @param profileId 访问网站的唯一标识
  * @param userId 访问网站的用户唯一标识：cookieid
  */
case class CombinedId(profileId: Int, userId: String) {
  val encode: String = {
    s"${userId.hashCode.toString.last}${profileId}${userId}"
  }
}
