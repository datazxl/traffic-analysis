package com.zxl.web

import com.zxl.parser.dataobject._
import com.zxl.parser.dataobject.dim.{SiteResourceInfo, TargetPageInfo}
import com.zxl.parser.utils.ParseUtils.{isNullOrEmptyOrDash, notNull}
import com.zxl.web.RichCollection._
import com.zxl.web.userinfo.UserVisitInfo
import eu.bitwalker.useragentutils.DeviceType
import org.apache.avro.generic.GenericRecord

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 对一个用户的所有session数据转化为最终实体DataRecords(用户级别)
  */
object SessionCompute {
  private var conversionIdIncrValue: Long = 0L

  /**
    * 对一个用户的所有session数据转化为最终实体DataRecords
    *
    * @param combinedId 用户id
    * @param sessions   session
    * @param sessionStartIndex
    * @return
    */
  def compute(combinedId: CombinedId, sessions: Seq[Seq[BaseDataObject]], sessionStartIndex: Long, userVisitInfoOpt: Option[UserVisitInfo]): (UserVisitInfo, Seq[DataRecords]) = {
    //1.将每一个会话中的所有DataObject进行归类（相同类型的用户访问行为数据放在一起，方便后面计算）
    val classifiedSessions: Seq[ClassifiedSession] = sessions.zipWithIndex.map { case (session, index) => {
      ClassifiedSession(index, session)
    }
    }

    val userVisitInfo = userVisitInfoOpt.getOrElse(UserVisitInfo(combinedId, 0, 0))

    //2.进行会话计算，将归类好的ClassifiedSession，计算为最终实体DataRecords
    val resRecords = ArrayBuffer.empty[DataRecords]
    classifiedSessions.foreach(classifiedSession => {
      // 计算会话ID
      val sessionId: Long = sessionStartIndex + classifiedSession.sessionIndex
      // 计算Session信息
      val session: Session = produceSession(sessionId, classifiedSession, userVisitInfo)
      // 计算PageView信息
      val pageViews: Seq[PageView] = producePageViews(classifiedSession, session)
      // 计算Heartbeat信息
      val heartBeats: Seq[Heartbeat] = produceHeartBeats(classifiedSession.hbMap, session)
      // 计算MouseClick信息
      val mouseClicks: Seq[MouseClick] = produceMouseClicks(classifiedSession.mcs, session)
      // 计算Conversion信息
      val conversions: Seq[Conversion] = produceConversions(session, classifiedSession.allActiveTargetInfo, classifiedSession.evs)

      resRecords += DataRecords(session, pageViews, mouseClicks, heartBeats, conversions)
    })

    (userVisitInfo, resRecords)
  }

  /**
    * 计算所有Conversion信息
    *
    * @param session        会话
    * @param targetInfoData 当前会话中所有TargetPageDataObject
    * @param eventData      当前会话中所有EventDataObject
    * @return Seq[Conversion]
    */
  private def produceConversions(session: Session,
                                 targetInfoData: Seq[(PvDataObject, TargetPageInfo)],
                                 eventData: Seq[EvDataObject]): Seq[Conversion] = {

    val event2Conversions: Seq[Conversion] = eventData map { event =>
      val conversion = new Conversion()
      conversionIdIncrValue += 1
      conversion.setConversionId(s"^!${session.getServerSessionId}_${conversionIdIncrValue}")
      conversion.setType("Event")
      conversion.setCategory(notNull(event.getEventCategory))
      conversion.setLabel(notNull(event.getEventLabel))
      conversion.setName(notNull(event.getEventAction))
      conversion.setConversionPageUrl(notNull(event.getUrl))
      conversion.setConversionPageTitle(notNull(event.getTitle))
      conversion.setConversionPageHostname(notNull(event.getHostDomain).toLowerCase)
      conversion.setConversionValue(event.getEventValue)
      conversion.setConversionServerTime(event.getServerTimeString)
      conversion.setPageViewId(event.getPvId)
      fillTimeRelated(conversion, event)
      conversion
    }

    val targetPage2Conversions: Seq[Conversion] = targetInfoData map { case (pvData, targetInfo) =>
      val conversion = new Conversion()
      conversionIdIncrValue += 1
      conversion.setConversionId(s"^!${session.getServerSessionId}_${conversionIdIncrValue}")
      conversion.setType("TargetPage")
      conversion.setName(targetInfo.getTargetName)
      conversion.setCategory("-")
      conversion.setLabel("-")
      conversion.setConversionPageUrl(pvData.getSiteResourceInfo.getUrl)
      conversion.setConversionPageTitle(pvData.getSiteResourceInfo.getPageTitle)
      conversion.setConversionPageHostname(notNull(pvData.getSiteResourceInfo.getDomain).toLowerCase)
      conversion.setConversionValue(1F)
      conversion.setConversionServerTime(pvData.getServerTimeString)
      conversion.setPageViewId(pvData.getPvId)
      fillTimeRelated(conversion, pvData)
      conversion
    }

    val conversions = targetPage2Conversions ++ event2Conversions

    conversions.map(conversion => {
      conversion.setTrackerVersion(session.getTrackerVersion)
      conversion.setProfileId(session.getProfileId)
      conversion.setUserId(session.getUserId)
      fillSession(conversion, session)
    })

    conversions
  }

  /**
    * 计算会话中所有MouseClick信息
    *
    * @param mcDatas 当前会话中的所有的dataObject
    * @param session 当前会话
    * @return Seq[MouseClick]
    */
  private def produceMouseClicks(mcDatas: Seq[McDataObject],
                                 session: Session): Seq[MouseClick] = {
    mcDatas.map(mcData => {
      val mc = new MouseClick()
      fillBase(mc, mcData)
      fillTimeRelated(mc, mcData)
      fillSession(mc, session)
      mc.setPageViewId(notNull(mcData.getPvId))
      mc.setMouseClickServerTime(notNull(mcData.getServerTimeString))
      mc.setClickPageUrl(notNull(mcData.getUrl))
      mc.setClickPageHostname(notNull(mcData.getPageHostName).toLowerCase)
      mc.setClickPageTitle(notNull(mcData.getPageTitle))
      mc.setClickPageOriginalUrl(notNull(mcData.getOriginalUrl))

      mc.setClickX(mcData.getClickX)
      mc.setClickY(mcData.getClickY)
      mc.setPageVersion(notNull(mcData.getPageVersion))
      mc.setRegionId(mcData.getPageRegion)
      mc.setSnapshotId(mcData.getSnapshotId)
      if (isNullOrEmptyOrDash(mcData.getClickScreenResolution))
        mc.setClickScreenResolution(session.getScreenResolution)
      else
        mc.setClickScreenResolution(mcData.getClickScreenResolution)

      mc.setLinkHostname(notNull(mcData.getLinkHostName).toLowerCase)
      mc.setLinkUrl(notNull(mcData.getLinkUrl))
      mc.setLinkText(notNull(mcData.getLinkText))
      mc.setLinkX(mcData.getLinkX)
      mc.setLinkY(mcData.getLinkY)
      mc.setLinkHeight(mcData.getLinkHeight)
      mc.setLinkWidth(mcData.getLinkWidth)
      mc.setIsLinkClicked(mcData.isLinkClicked)
      mc
    })
  }

  /**
    * 计算会话中所有HearBeat信息
    *
    * @param hbDataObjects 当前会话中的所有的HbDataObject
    * @param session       当前会话
    * @return Seq[Heartbeat]
    */
  private def produceHeartBeats(hbDataObjects: mutable.Map[String, HbDataObject], session: Session): Seq[Heartbeat] = {
    hbDataObjects.values.map(hbData => {
      val hb = new Heartbeat()
      fillBase(hb, hbData)
      hb.setServerSessionId(session.getServerSessionId)
      hb.setServerTime(notNull(hbData.getServerTimeString))
      hb.setLoadingDuration(hbData.getLoadingDuration)
      hb.setPageViewId(hbData.getPvId)
      hb
    }).toSeq
  }

  /**
    * 计算会话中所有PageView信息
    *
    * @param data    当前会话中的所有的dataObject
    * @param session 当前会话
    * @return Seq[PageView]
    */
  private def producePageViews(data: ClassifiedSession, session: Session): Seq[PageView] = {

    //==========需要计算的字段===========
    val pvDatas = data.pvs

    val (_, pageViewSeq, _) = pvDatas.zipWithIndex.foldLeft(1, new ArrayBuffer[PageView], null: PvDataObject) {
      //pageViewDepth表示页面访问深度，pageViewSeq表示计算后的PageView的列表
      //previous表示前一个pv
      //currentPv表示当前pv， currentIndex当前pv在所有pv中的index
      case ((pageViewDepth, pageViewSeq, prePvData), (currentPvData, currentIndex)) =>
        val pv = new PageView()

        //获取当前pv对应的hb,计算页面加载时长
        val currentPvHbOpt = data.hbMap.get(currentPvData.getPvId)
        val loading = currentPvHbOpt match {
          case Some(hb) => hb.getLoadingDuration
          case None => 0
        }
        pv.setLoadingDuration(loading)
        pv.setAccessOrder(currentIndex + 1) //页面的访问顺序
        pv.setPageDuration(currentPvData.getDuration) //页面的停留时长，已经在计算会话停留时长的时候计算过了
        pv.setIsExitPage(currentIndex >= pvDatas.length - 1) //判断是否是退出页

        //计算页面访问深度
        val nextDepth =
          if (prePvData == null) { //如果当前是第一个pv，页面访问深度为1
            pageViewDepth
          } else { //和前一个pv页面对比，如果相同，则页面访问深度不变
            if (currentPvData.getSiteResourceInfo.getUrl.equals(prePvData.getSiteResourceInfo.getUrl)) {
              pv.setIsRefresh(true)
              pageViewDepth
            } else {
              pageViewDepth + 1
            }
          }
        pv.setPageViewDepth(nextDepth)

        //==========其他ParserLog已经解析完成的字段===========
        pv.setPageViewId(currentPvData.getPvId)
        pv.setPageViewServerTime(currentPvData.getServerTimeString)
        pv.setPageUrl(currentPvData.getSiteResourceInfo.getUrl)
        pv.setPageOriginalUrl(currentPvData.getSiteResourceInfo.getOriginalUrl)
        pv.setPageHostname(currentPvData.getSiteResourceInfo.getDomain)
        pv.setPageTitle(currentPvData.getSiteResourceInfo.getPageTitle)
        pv.setPageViewReferrerUrl(currentPvData.getReferrerInfo.getUrl)
        fillBase(pv, currentPvData) //基本字段的填充
        fillTimeRelated(pv, currentPvData) //时间相关字段的填充
        fillSession(pv, session) //派生会话字段

        pageViewSeq += pv
        (nextDepth, pageViewSeq, currentPvData)
    }
    pageViewSeq
  }

  private def fillBase(record: GenericRecord, base: BaseDataObject): Unit = {
    record.put("tracker_version", base.getTrackerVersion)
    record.put("profile_id", base.getProfileId)
    record.put("user_id", notNull(base.getUserId))
  }

  private def fillTimeRelated(record: GenericRecord, base: BaseDataObject): Unit = {
    record.put("hour_of_day", base.getHourOfDay)
    record.put("month_of_year", base.getMonthOfYear)
    record.put("day_of_week", base.getDayOfWeek)
    record.put("week_of_year", base.getWeekOfYear)
  }

  private def fillSession(record: GenericRecord, session: Session) {
    record.put("days_since_last_visit", session.getDaysSinceLastVisit)
    record.put("user_visit_number", session.getUserVisitNumber)
    record.put("is_new_visitor", session.getIsNewVisitor)

    record.put("server_session_id", session.getServerSessionId)
    record.put("session_duration", session.getSessionDuration)
    record.put("session_server_time", session.getSessionServerTime)

    record.put("referrer_url", session.getReferrerUrl)
    record.put("referrer_hostname", session.getReferrerHostname)
    record.put("source_type", session.getSourceType)
    record.put("channel_name", session.getChannelName)
    record.put("search_engine", session.getSearchEngine)
    record.put("keywords", session.getKeywords)
    record.put("keyword_id", session.getKeywordId)

    record.put("ad_campaign", session.getAdCampaign)
    record.put("ad_channel", session.getAdChannel)
    record.put("ad_source", session.getAdSource)
    record.put("ad_medium", session.getAdMedium)
    record.put("ad_keywords", session.getAdKeywords)
    record.put("ad_content", session.getAdContent)
    record.put("is_paid_traffic", session.getIsPaidTraffic)

    record.put("user_agent", session.getUserAgent)
    record.put("browser_brief", session.getBrowserBrief)
    record.put("browser_detail", session.getBrowserDetail)
    record.put("is_mobile", session.getIsMobile)
    record.put("browser_language", session.getBrowserLanguage)
    record.put("device_brand", session.getDeviceBrand)
    record.put("device_type", session.getDeviceType)
    record.put("device_name", session.getDeviceName)
    record.put("screen_resolution", session.getScreenResolution)
    record.put("color_depth", session.getColorDepth)
    record.put("flash_version", session.getFlashVersion)
    record.put("silverlight_version", session.getSilverlightVersion)
    record.put("java_enabled", session.getJavaEnabled)
    record.put("cookie_enabled", session.getCookieEnabled)
    record.put("alexa_toolbar_installed", session.getAlexaToolbarInstalled)
    record.put("os_language", session.getOsLanguage)

    record.put("country", session.getCountry)
    record.put("province", session.getProvince)
    record.put("city", session.getCity)
    record.put("longitude", session.getLongitude)
    record.put("latitude", session.getLatitude)
    record.put("client_ip", session.getClientIp)

    record.put("landing_page_url", session.getLandingPageUrl)
    record.put("landing_page_original_url", session.getLandingPageOriginalUrl)
    record.put("landing_page_hostname", session.getLandingPageTitle)
    record.put("landing_page_title", session.getLandingPageTitle)
    record.put("second_page_url", session.getSecondPageUrl)
    record.put("second_page_original_url", session.getSecondPageOriginalUrl)
    record.put("second_page_hostname", session.getSecondPageHostname)
    record.put("second_page_title", session.getSecondPageTitle)
    record.put("exit_page_url", session.getExitPageUrl)
    record.put("exit_page_original_url", session.getExitPageOriginalUrl)
    record.put("exit_page_hostname", session.getExitPageHostname)
    record.put("exit_page_title", session.getExitPageTitle)

    record.put("is_bounced", session.getIsBounced)
    record.put("pv_count", session.getPvCount)
    record.put("pv_distinct_count", session.getPvDistinctCount)
    record.put("conversion_count", session.getConversionCount)
    record.put("event_count", session.getEventCount)
    record.put("event_distinct_count", session.getEventDistinctCount)
    record.put("target_count", session.getTargetCount)
    record.put("target_distinct_count", session.getTargetDistinctCount)
    record.put("mouse_click_count", session.getMouseClickCount)
  }

  /**
    * 计算Session
    *
    * @param sessionId 会话id
    * @param data      会话中所有的DataObject
    * @return Session
    */
  private def produceSession(sessionId: Long, data: ClassifiedSession, userVisitInfo: UserVisitInfo): Session = {
    val session = new Session()
    session.setServerSessionId(sessionId)
    //==========需要计算的字段===========
    //计算是否是新的访客
    if (userVisitInfo.lastVisitTime == 0) {
      session.setIsNewVisitor(true)
    } else {
      session.setIsNewVisitor(false)
    }
    //计算这个访客自从上次访问到这次访问中间隔了多少天
    if (userVisitInfo.lastVisitTime == 0){
      session.setDaysSinceLastVisit(-1)
    } else {
      session.setDaysSinceLastVisit(((data.sessionStartTime - userVisitInfo.lastVisitTime) / 1000 / 60 / 60 / 24).toInt)
    }
    session.setUserVisitNumber(userVisitInfo.lastVisitCount + 1)  //访客访问的次数

    //跟新用户访问行为数据
    userVisitInfo.lastVisitCount += 1
    userVisitInfo.lastVisitTime = data.sessionStartTime

    //计算会话停留时长
    session.setSessionDuration(data.fetchSessionDuration)

    //计算会话特定页面维度
    val (landingPageViewInfo, secondPageViewInfo, exitPagePageViewInfo) =
      getPageViewInfos(data.pvs, data.paidPVOpt)

    session.setLandingPageUrl(landingPageViewInfo.url)
    session.setLandingPageOriginalUrl(landingPageViewInfo.originalUrl)
    session.setLandingPageHostname(landingPageViewInfo.hostName)
    session.setLandingPageTitle(landingPageViewInfo.title)

    session.setSecondPageUrl(secondPageViewInfo.url)
    session.setSecondPageOriginalUrl(secondPageViewInfo.originalUrl)
    session.setSecondPageHostname(secondPageViewInfo.hostName)
    session.setSecondPageTitle(secondPageViewInfo.title)

    session.setExitPageUrl(exitPagePageViewInfo.url)
    session.setExitPageOriginalUrl(exitPagePageViewInfo.originalUrl)
    session.setExitPageHostname(exitPagePageViewInfo.hostName)
    session.setExitPageTitle(exitPagePageViewInfo.title)

    //会话实体统计维度
    session.setPvCount(data.pvs.length)
    session.setPvDistinctCount(data.pvs.distinctBy(_.getSiteResourceInfo.getUrl).length)
    session.setIsBounced(data.pvs.size == 1)
    session.setMouseClickCount(data.mcs.length)
    session.setTargetCount(data.allActiveTargetInfo.length)
    session.setEventCount(data.evs.length)
    session.setConversionCount(data.evs.length + data.allActiveTargetInfo.length)
    session.setTargetDistinctCount(data.allActiveTargetInfo.distinctBy { case (_, info) => info.getKey }.length)
    session.setEventDistinctCount(data.evs.distinctBy(e => (e.getEventCategory, e.getEventLabel, e.getEventAction)).length)

    //==========其他ParserLog已经解析完成的字段===========
    //通用字段
    val firstDataObj: BaseDataObject = data.firstDataObj
    session.setTrackerVersion(notNull(firstDataObj.getTrackerVersion))
    session.setProfileId(firstDataObj.getProfileId)
    session.setUserId(firstDataObj.getUserId)

    //时间维度
    session.setSessionServerTime(notNull(firstDataObj.getServerTimeString))
    session.setHourOfDay(firstDataObj.getHourOfDay)
    session.setMonthOfYear(firstDataObj.getMonthOfYear)
    session.setWeekOfYear(firstDataObj.getWeekOfYear)
    session.setDayOfWeek(firstDataObj.getDayOfWeek)

    //位置维度
    session.setCountry(notNull(firstDataObj.getIpLocation.getCountry))
    session.setProvince(notNull(firstDataObj.getIpLocation.getRegion))
    session.setCity(notNull(firstDataObj.getIpLocation.getCity))
    val longitude = firstDataObj.getIpLocation.getLongitude
    session.setLongitude(if (isNullOrEmptyOrDash(longitude)) longitude.toFloat else 0F)
    val latitude = firstDataObj.getIpLocation.getLatitude
    session.setLatitude(if (isNullOrEmptyOrDash(latitude)) longitude.toFloat else 0F)
    session.setClientIp(firstDataObj.getClientIp)

    val headPV = data.pvs.head
    val pv = data.paidPVOpt getOrElse headPV

    //来源维度
    session.setReferrerUrl(pv.getReferrerInfo.getUrl)
    session.setReferrerHostname(notNull(pv.getReferrerInfo.getDomain).toLowerCase)
    session.setSourceType(notNull(pv.getReferrerInfo.getReferType))
    session.setChannelName(notNull(pv.getReferrerInfo.getChannel))
    session.setSearchEngine(notNull(pv.getReferrerInfo.getSearchEngineName))
    session.setKeywords(notNull(pv.getReferrerInfo.getKeyword))
    session.setKeywordId(notNull(pv.getReferrerInfo.getEqId))

    //广告维度
    val adInfo = pv.getAdInfo
    session.setAdCampaign(notNull(adInfo.getUtmCampaign))
    session.setAdChannel(notNull(adInfo.getUtmChannel))
    session.setAdSource(notNull(adInfo.getUtmSource))
    session.setAdMedium(notNull(adInfo.getUtmMedium))
    session.setAdKeywords(notNull(adInfo.getUtmTerm))
    session.setAdContent(notNull(adInfo.getUtmContent))
    session.setIsPaidTraffic(adInfo.isPaid)

    //系统以及浏览器维度
    session.setUserAgent(notNull(firstDataObj.getUserAgent))
    session.setBrowserBrief(notNull(firstDataObj.getUserAgentInfo.getBrowser.getName))
    session.setBrowserDetail(notNull(firstDataObj.getUserAgentInfo.getBrowser.toString))
    session.setDeviceBrand(notNull(firstDataObj.getUserAgentInfo.getOperatingSystem.getManufacturer.getName))
    session.setIsMobile(firstDataObj.getUserAgentInfo.getOperatingSystem.getDeviceType == DeviceType.MOBILE)
    session.setDeviceType(notNull(firstDataObj.getUserAgentInfo.getOperatingSystem.getDeviceType.getName))
    session.setDeviceName(firstDataObj.getUserAgentInfo.getOperatingSystem.getName)
    session.setScreenResolution(notNull(pv.getBrowserInfo.getResolution))
    session.setColorDepth(notNull(pv.getBrowserInfo.getColorDepth))
    session.setFlashVersion(notNull(pv.getBrowserInfo.getFlashVersion))
    session.setSilverlightVersion(notNull(pv.getBrowserInfo.getSilverlightVersion))
    session.setJavaEnabled(pv.getBrowserInfo.isJavaEnable)
    session.setCookieEnabled(pv.getBrowserInfo.isCookieEnable)
    session.setAlexaToolbarInstalled(pv.getBrowserInfo.isAlexaToolBar)
    session.setOsLanguage(notNull(pv.getBrowserInfo.getOsLanguage))
    session.setBrowserLanguage(notNull(pv.getBrowserInfo.getBrowserLanguage))
    //如果pv中已经含有设备类型和设备名称的话，则优先选择
    if (!isNullOrEmptyOrDash(pv.getBrowserInfo.getDeviceType)) {
      session.setDeviceType(notNull(pv.getBrowserInfo.getDeviceType))
    }
    if (!isNullOrEmptyOrDash(pv.getBrowserInfo.getDeviceName)) {
      session.setDeviceName(notNull(pv.getBrowserInfo.getDeviceName))
    }

    session
  }

  private def getPageViewInfos(pvArray: Seq[PvDataObject], paidPVOpt: Option[PvDataObject]
                              ): (PageViewInfo, PageViewInfo, PageViewInfo) = pvArray match {
    //1. 如果只有一个pv的话，则这个pv是着陆页也是退出页
    case Seq(onlyOnePage) =>
      val info: SiteResourceInfo = onlyOnePage.getSiteResourceInfo
      val pageViewInfo = PageViewInfo(info.getUrl, info.getOriginalUrl, info.getDomain, info.getPageTitle)
      (pageViewInfo, PageViewInfo.default, pageViewInfo)

    //2. 如果含有2个pv或者以上的情况
    case Seq(firstPv, secondPv, _*) => {
      // 着陆页pv先取是重要入口的pv，如果没有重要入口pv的话就取首个pv
      val firstPvSiteResourceInfo = paidPVOpt.getOrElse(firstPv).getSiteResourceInfo
      // 第二页不能取重要入口：如果第二个PV是重要入口，那么取第一个PV
      val secondPvSiteResourceInfo = if (paidPVOpt.nonEmpty && firstPv.isDifferentFrom(paidPVOpt.get)) firstPv.getSiteResourceInfo else secondPv.getSiteResourceInfo
      // 最后一页不能取重要入口：如果最后一个PV是重要入口，那么取倒数第二个PV。
      val lastPv = pvArray.last
      val lastPvSiteResourceInfo = if (paidPVOpt.nonEmpty && lastPv.isDifferentFrom(paidPVOpt.get)) lastPv.getSiteResourceInfo else pvArray(pvArray.length - 2).getSiteResourceInfo

      (PageViewInfo(firstPvSiteResourceInfo.getUrl, firstPvSiteResourceInfo.getOriginalUrl, firstPvSiteResourceInfo.getDomain, firstPvSiteResourceInfo.getPageTitle),
        PageViewInfo(secondPvSiteResourceInfo.getUrl, secondPvSiteResourceInfo.getOriginalUrl, secondPvSiteResourceInfo.getDomain, secondPvSiteResourceInfo.getPageTitle),
        PageViewInfo(lastPvSiteResourceInfo.getUrl, lastPvSiteResourceInfo.getOriginalUrl, lastPvSiteResourceInfo.getDomain, lastPvSiteResourceInfo.getPageTitle))
    }
    case _ => (PageViewInfo.default, PageViewInfo.default, PageViewInfo.default)
  }
}

//会话特定的页面维度
private case class PageViewInfo(url: String, originalUrl: String, hostName: String, title: String)

private object PageViewInfo {
  val default = PageViewInfo("-", "-", "-", "-")
}