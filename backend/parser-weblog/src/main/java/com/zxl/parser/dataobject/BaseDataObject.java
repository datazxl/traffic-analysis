package com.zxl.parser.dataobject;

import com.zxl.parser.iplocation.IpLocation;
import com.zxl.parser.utils.DateUtils;
import eu.bitwalker.useragentutils.UserAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 存放用户访问行为公共字段
 */
public class BaseDataObject implements ParsedDataObject {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int profileId; //网站id
    private String trackerVersion; //javaScript版本
    private String command; //访问数据类型
    private String userId;//cookie标识唯一用户
    private String pvId;//页面唯一标识
    private String serverTimeString;//发送日志时服务器时间
    private Date serverTime;
    private Calendar calendar;
    private String userAgent;//访客系统环境
    private UserAgent userAgentInfo;//访客系统环境
    private String clientIp;//客户端ip
    private IpLocation ipLocation; //客户端地址位置

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public IpLocation getIpLocation() {
        return ipLocation;
    }

    public void setIpLocation(IpLocation ipLocation) {
        this.ipLocation = ipLocation;
    }

    public UserAgent getUserAgentInfo() {
        return userAgentInfo;
    }

    public void setUserAgentInfo(UserAgent userAgentInfo) {
        this.userAgentInfo = userAgentInfo;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getTrackerVersion() {
        return trackerVersion;
    }

    public void setTrackerVersion(String trackerVersion) {
        this.trackerVersion = trackerVersion;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPvId() {
        return pvId;
    }

    public void setPvId(String pvId) {
        this.pvId = pvId;
    }

    public String getServerTimeString() {
        return serverTimeString;
    }

    public void setServerTimeString(String serverTimeString) {
        this.serverTimeString = serverTimeString;
        try {
            this.serverTime = dateFormat.parse(serverTimeString);
            calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTime(serverTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getServerTime() {
        return serverTime;
    }

    public int getHourOfDay() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public String getDayOfWeek() {
        return DateUtils.getChineseWeekStr(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public int getMonthOfYear() {
        return calendar.get(Calendar.MONTH);
    }

    public int getWeekOfYear() {
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }


    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
