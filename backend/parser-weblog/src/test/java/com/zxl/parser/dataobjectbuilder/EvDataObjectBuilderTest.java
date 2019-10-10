package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.EvDataObject;
import com.zxl.preparser.PreParsedLog;
import com.zxl.preparser.PreParserWebLog;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EvDataObjectBuilderTest {

    @Test
    public void doBuildDataObject() {
        String weblog = "2018-06-15 13:43:39 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=ev&gssrvid=GWD-000702&gsuid=29137384iqrvzn66&gssid=291373841jjmbp66" +
                "&pvid=29137472grze7w28&gsltime=1529166280037&gstmzone=8&gsdelay=6130&rd=czuh8&eca=Checkout" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&gstl=%E8%B4%AD%E7%89%A9%E8%BD%A6%20-%20Under%20Armour%20%E5%AE%98%E6%96%B9%E8%B4%AD%E7%89%A9%E7%BD%91%E7%AB%99" +
                "&dedupid=29137480w2fo2j28&gsourl=https%3A%2F%2Fwww.underarmour.cn%2FmyShoppingCart.htm 80 - 58.210.35.226 " +
                "Mozilla/5.0+(Windows+NT+10.0;+WOW64;+Trident/7.0;+LCTE;+rv:11.0)+like+Gecko";
        PreParsedLog preParsedLog = PreParserWebLog.parse(weblog);
        List<BaseDataObject> baseDataObjects = new EvDataObjectBuilder().doBuildDataObjects(preParsedLog);
        Assert.assertEquals(1, baseDataObjects.size());
        EvDataObject evDataObject = (EvDataObject) baseDataObjects.get(0);
        Assert.assertEquals("Checkout", evDataObject.getEventCategory());
        Assert.assertEquals("-", evDataObject.getEventAction());
        Assert.assertEquals("-", evDataObject.getEventLabel());
        Assert.assertEquals(0F, evDataObject.getEventValue(), 0);
        Assert.assertEquals("-", evDataObject.getUrl());
        Assert.assertEquals("https://www.underarmour.cn/myShoppingCart.htm", evDataObject.getOriginalUrl());
        Assert.assertEquals("购物车 - Under Armour 官方购物网站", evDataObject.getTitle());
        Assert.assertEquals("-", evDataObject.getHostDomain());
    }
}