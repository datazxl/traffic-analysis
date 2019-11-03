package com.zxl.web

import com.zxl.parser.dataobject._
import com.zxl.parser.dataobject.dim.{AdInfo, ReferrerInfo, SiteResourceInfo}
import org.scalatest.FunSuite

class SessionIncisionTest extends FunSuite {

  test("testGetSessions") {
    val noAdInfo = new AdInfo

    val dataObject1 = new PvDataObject
    dataObject1.setProfileId(1)
    dataObject1.setUserId("user1")
    dataObject1.setServerTimeString("2018-06-15 13:20:30")
    val dataObject1Refer = new ReferrerInfo
    dataObject1Refer.setDomain("refer.test.com")
    dataObject1Refer.setUrl("http://refer.test.com/object1")
    val dataObject1SiteResourceInfo = new SiteResourceInfo
    dataObject1SiteResourceInfo.setUrl("http://site.test.com/object1")
    dataObject1SiteResourceInfo.setDomain("site.test.com")
    dataObject1.setReferrerInfo(dataObject1Refer)
    dataObject1.setSiteResourceInfo(dataObject1SiteResourceInfo)
    dataObject1.setAdInfo(noAdInfo)

    val dataObject2 = new McDataObject
    dataObject2.setProfileId(1)
    dataObject2.setUserId("user1")
    dataObject2.setServerTimeString("2018-06-15 13:21:30")

    val dataObject3 = new McDataObject
    dataObject3.setProfileId(1)
    dataObject3.setUserId("user1")
    dataObject3.setServerTimeString("2018-06-15 13:21:45")

    val dataObject4 = new HbDataObject
    dataObject4.setProfileId(1)
    dataObject4.setUserId("user1")
    dataObject4.setServerTimeString("2018-06-15 13:22:30")

    //重要入口pv
    val dataObject5 = new PvDataObject
    dataObject5.setProfileId(1)
    dataObject5.setUserId("user1")
    dataObject5.setServerTimeString("2018-06-15 13:29:31")
    val dataObject5Refer = new ReferrerInfo
    dataObject5Refer.setDomain("www.baidu.com")
    val dataObject5SiteResourceInfo = new SiteResourceInfo
    dataObject5SiteResourceInfo.setDomain("www.51cto.com")
    val adInfo = new AdInfo
    adInfo.setUtmSource("baidu")
    dataObject5.setReferrerInfo(dataObject5Refer)
    dataObject5.setSiteResourceInfo(dataObject5SiteResourceInfo)
    dataObject5.setAdInfo(adInfo)


    val dataObject7 = new McDataObject
    dataObject7.setProfileId(1)
    dataObject7.setUserId("user1")
    dataObject7.setServerTimeString("2018-06-15 13:30:10")

    val dataObject8 = new HbDataObject
    dataObject8.setProfileId(1)
    dataObject8.setUserId("user1")
    dataObject8.setServerTimeString("2018-06-15 13:31:12")

    val dataObject9 = new PvDataObject
    dataObject9.setProfileId(1)
    dataObject9.setUserId("user1")
    dataObject9.setServerTimeString("2018-06-15 19:22:31")
    val dataObject9Refer = new ReferrerInfo
    dataObject9Refer.setDomain("refer.test.com")
    dataObject9Refer.setUrl("http://refer.test.com/object9")
    val dataObject9SiteResourceInfo = new SiteResourceInfo
    dataObject9SiteResourceInfo.setUrl("http://site.test.com/object9")
    dataObject9SiteResourceInfo.setDomain("site.test.com")
    dataObject9.setReferrerInfo(dataObject9Refer)
    dataObject9.setSiteResourceInfo(dataObject9SiteResourceInfo)
    dataObject9.setAdInfo(noAdInfo)

    val dataObject10 = new McDataObject
    dataObject10.setProfileId(1)
    dataObject10.setUserId("user1")
    dataObject10.setServerTimeString("2018-06-15 19:22:33")

    val dataObject11 = new HbDataObject
    dataObject11.setProfileId(1)
    dataObject11.setUserId("user1")
    dataObject11.setServerTimeString("2018-06-15 19:22:39")

    val dataObject12 = new PvDataObject
    dataObject12.setProfileId(1)
    dataObject12.setUserId("user1")
    dataObject12.setServerTimeString("2018-06-15 22:20:21")
    val dataObject12Refer = new ReferrerInfo
    dataObject12Refer.setDomain("refer.test.com")
    dataObject12Refer.setUrl("http://refer.test.com/object12")
    val dataObject12SiteResourceInfo = new SiteResourceInfo
    dataObject12SiteResourceInfo.setUrl("http://site.test.com/object12")
    dataObject12SiteResourceInfo.setDomain("site.test.com")
    dataObject12.setReferrerInfo(dataObject12Refer)
    dataObject12.setSiteResourceInfo(dataObject12SiteResourceInfo)
    dataObject12.setAdInfo(noAdInfo)

    val dataObject13 = new McDataObject
    dataObject13.setProfileId(1)
    dataObject13.setUserId("user1")
    dataObject13.setServerTimeString("2018-06-15 22:20:22")

    val dataObject14 = new McDataObject
    dataObject14.setProfileId(1)
    dataObject14.setUserId("user1")
    dataObject14.setServerTimeString("2018-06-15 22:20:23")

    val dataObject15 = new HbDataObject
    dataObject15.setProfileId(1)
    dataObject15.setUserId("user1")
    dataObject15.setServerTimeString("2018-06-15 22:20:25")

    val dataObject16 = new PvDataObject
    dataObject16.setProfileId(1)
    dataObject16.setUserId("user1")
    dataObject16.setServerTimeString("2018-06-15 22:20:27")
    val dataObject16Refer = new ReferrerInfo
    dataObject16Refer.setDomain("refer.test.com")
    dataObject16Refer.setUrl("http://refer.test.com/object16")
    val dataObject16SiteResourceInfo = new SiteResourceInfo
    dataObject16SiteResourceInfo.setUrl("http://site.test.com/object16")
    dataObject16SiteResourceInfo.setDomain("site.test.com")
    dataObject16.setReferrerInfo(dataObject16Refer)
    dataObject16.setSiteResourceInfo(dataObject16SiteResourceInfo)
    dataObject16.setAdInfo(noAdInfo)

    val dataObject17 = new EvDataObject
    dataObject17.setProfileId(1)
    dataObject17.setUserId("user1")
    dataObject17.setServerTimeString("2018-06-15 22:20:28")

    val dataObject18 = new HbDataObject
    dataObject18.setProfileId(1)
    dataObject18.setUserId("user1")
    dataObject18.setServerTimeString("2018-06-15 22:20:30")

    //重要入口pv
    val dataObject19 = new PvDataObject
    dataObject19.setProfileId(1)
    dataObject19.setUserId("user1")
    dataObject19.setServerTimeString("2018-06-15 22:45:31")
    val dataObject19Refer = new ReferrerInfo
    dataObject19Refer.setDomain("www.baidu.com")
    val dataObject19SiteResourceInfo = new SiteResourceInfo
    dataObject19SiteResourceInfo.setDomain("www.51cto.com")
    val adInfo2 = new AdInfo
    adInfo2.setUtmSource("baidu")
    dataObject19.setAdInfo(adInfo2)
    dataObject19.setReferrerInfo(dataObject19Refer)
    dataObject19.setSiteResourceInfo(dataObject19SiteResourceInfo)

    val dataObject20 = new HbDataObject
    dataObject20.setProfileId(1)
    dataObject20.setUserId("user1")
    dataObject20.setServerTimeString("2018-06-15 22:46:30")

    val sortedDataObjects = Seq(dataObject1, dataObject2, dataObject3, dataObject4, dataObject5, dataObject7, dataObject8,
      dataObject9, dataObject10, dataObject11, dataObject12, dataObject13, dataObject14, dataObject15, dataObject16, dataObject17,
      dataObject18, dataObject19, dataObject20)

    val sessions: Seq[Seq[BaseDataObject]] = SessionIncision.getSessions(sortedDataObjects)

    sessions.foreach(seq => {
      println(seq.toList)
    })
    assert(5 == sessions.length)
  }
}