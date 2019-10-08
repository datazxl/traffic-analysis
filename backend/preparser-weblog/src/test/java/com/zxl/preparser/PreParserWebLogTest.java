package com.zxl.preparser;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class PreParserWebLogTest {

    @Test
    public void parse() {
        String log = "2018-06-15 13:41:39 10.200.200.98 GET " +
                "/gs.gif gsver=3.8.0.9&gscmd=pv&gssrvid=GWD-000702&gsuid=28872593x97" +
                "69t21&gssid=t291319151wwp6d11&pvid=291355119hvjhz11&gsltime=1529164" +
                "311206&gstmzone=8&rd=i3u8k&gstl=Under%20Armour%7C%E5%AE%89%E5%BE%B7%" +
                "E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91%20-%20UA%E8%BF%90%E5%8A" +
                "%A8%E5%93%81%E7%89%8C%E4%B8%93%E5%8D%96%EF%BC%8C%E7%BE%8E%E5%9B%BD%E9" +
                "%AB%98%E7%AB%AF%E8%BF%90%E5%8A%A8%E7%A7%91%E6%8A%80%E5%93%81%E7%89%8C&" +
                "gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520" +
                "exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520no" +
                "t%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist.&gsce=1&gsclr=24&gsj" +
                "=0&gsst=0&gswh=759&gsph=5461&gspw=1519&gssce=1&gsscr=1536*864&dedupid=2" +
                "9135511vx5ccp11&gsurl=https%3A%2F%2Fwww.underarmour.cn%2F&gsorurl=https%3A%2F%2Fwww.underarmour.cn " +
                "80 - 58.210.35.226 Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";
        PreParsedLog parsedLog = PreParserWebLog.parse(log);
        assertEquals("2018-06-15 13:41:39", parsedLog.getServerTime());
        assertEquals("10.200.200.98", parsedLog.getServerIp());
        assertEquals("GET", parsedLog.getMethod());
        assertEquals("/gs.gif", parsedLog.getUriStem());
        assertEquals("pv", parsedLog.getCommand());
        assertEquals(702, parsedLog.getProfileId());
        assertEquals(80, parsedLog.getServerPort());
        assertEquals("58.210.35.226", parsedLog.getClientIp());
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36", parsedLog.getUserAgent());
        assertEquals(2018, parsedLog.getYear());
        assertEquals(201806, parsedLog.getMonth());
        assertEquals(20180615, parsedLog.getDay());
    }
}