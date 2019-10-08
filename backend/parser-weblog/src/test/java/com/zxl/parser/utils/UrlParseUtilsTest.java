package com.zxl.parser.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class UrlParseUtilsTest {

    @Test
    public void getInfoFromUrl_1() {
        //规则的url，含有fragment，且fragment在query的后面
        String url = "https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#NewLaunch";
        UrlInfo info = UrlParseUtils.getInfoFromUrl(url);
        Assert.assertEquals("qf=11-149&pf=&sortStr=&nav=640", info.getQuery());
        Assert.assertEquals("https", info.getScheme());
        Assert.assertEquals("www.underarmour.cn", info.getDomain());
        Assert.assertEquals("/s-HOVR", info.getPath());
        Assert.assertEquals("NewLaunch", info.getFragment());
    }

    @Test
    public void getInfoFromUrl_2() {
        //规则的url，含有fragment，且fragment在query的前面
        String url = "https://www.underarmour.cn/s-HOVR#NewLaunch?qf=11-149&pf=&sortStr=&nav=640";
        UrlInfo info = UrlParseUtils.getInfoFromUrl(url);
        Assert.assertEquals("qf=11-149&pf=&sortStr=&nav=640", info.getQuery());
        Assert.assertEquals("https", info.getScheme());
        Assert.assertEquals("www.underarmour.cn", info.getDomain());
        Assert.assertEquals("/s-HOVR", info.getPath());
        Assert.assertEquals("NewLaunch", info.getFragment());
    }

    @Test
    public void getInfoFromUrl_3() {
        //不规则的url，含有fragment，但是不含有query
        String url = "https://www.underarmour.cn/s-HOVR#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607";
        UrlInfo info = UrlParseUtils.getInfoFromUrl(url);
        Assert.assertEquals("-", info.getQuery());
        Assert.assertEquals("https", info.getScheme());
        Assert.assertEquals("www.underarmour.cn", info.getDomain());
        Assert.assertEquals("/s-HOVR", info.getPath());
        Assert.assertEquals("44-1|NewLaunch|HOVR|HOVR|HOVR|201800607", info.getFragment());
    }

    @Test
    public void getInfoFromUrl_4() {
        //不规则的url，含有fragment,含有query
        String url = "https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607";
        UrlInfo info = UrlParseUtils.getInfoFromUrl(url);
        Assert.assertEquals("qf=11-149&pf=&sortStr=&nav=640", info.getQuery());
        Assert.assertEquals("https", info.getScheme());
        Assert.assertEquals("www.underarmour.cn", info.getDomain());
        Assert.assertEquals("/s-HOVR", info.getPath());
        Assert.assertEquals("44-1|NewLaunch|HOVR|HOVR|HOVR|201800607", info.getFragment());
    }

    @Test
    public void getInfoFromUrl_5() {
        //不规则的url的解析, 不含有fragment,含有query
        String url = "http://m.baidu.com:8080/from=1012637v/pu=sz%401320_480%2Ccuid%40_PHful8jS8_MuvtqgaHai_iaHalh8vi20aHda_OD2a8Euv8xga-18_uQvt_Ra2tDA%2Ccua%40_a-qi4ujvfg4NE6pI5me6NIy2IgUI2tYAC_uB%2Ccut%405kSYMltqeupciXM9ravjh_h0vCgcuDPWpi3pur_aC%2Cosname%40baiduboxapp%2Cctv%402%2Ccfrom%401012637v%2Ccen%40cuid_cua_cut%2Ccsrc%40app_mainbox_txt%2Cvmgdb%400020100228y/s?tn=zbios";
        UrlInfo info = UrlParseUtils.getInfoFromUrl(ParseUtils.decode(url));
        Assert.assertEquals("tn=zbios", info.getQuery());
        Assert.assertEquals("http", info.getScheme());
        Assert.assertEquals("m.baidu.com:8080", info.getDomain());
        Assert.assertEquals("/from=1012637v/pu=sz@1320_480,cuid@_PHful8jS8_MuvtqgaHai_iaHalh8vi20aHda_OD2a8Euv8xga-18_uQvt_Ra2tDA,cua@_a-qi4ujvfg4NE6pI5me6NIy2IgUI2tYAC_uB,cut@5kSYMltqeupciXM9ravjh_h0vCgcuDPWpi3pur_aC,osname@baiduboxapp,ctv@2,cfrom@1012637v,cen@cuid_cua_cut,csrc@app_mainbox_txt,vmgdb@0020100228y/s", info.getPath());
    }
    @Test
    public void getQueryParams() {
        Map<String, String> map = UrlParseUtils.getQueryParams("qf=11-149&pf=&sortStr=&nav=640");
        Assert.assertEquals(4, map.size());
        Assert.assertEquals("11-149", map.get("qf"));
        Assert.assertEquals("640", map.get("nav"));
        Assert.assertEquals("-", map.get("pf"));
    }
}