package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.PvDataObject;
import com.zxl.parser.dataobject.TargetPageDataObject;
import com.zxl.parser.dataobject.dim.TargetPageInfo;
import com.zxl.preparser.PreParsedLog;
import com.zxl.preparser.PreParserWebLog;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class PvDataObjectBuilderTest {

    @Test
    public void doBuildDataObjects() {
        String weblog = "2018-06-15 18:43:27 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=pv&gssrvid=GWD-000702&gsuid=28872593x9769t21&gssid=29138614b1vulq13" +
                "&pvid=29139124zhn5ps17&gsltime=1529167924411&gstmzone=8&rd=hz64j" +
                "&gstl=UA%E7%94%B7%E5%A3%AB%E8%B7%91%E6%AD%A5%E8%BF%90%E5%8A%A8%E9%9E%8B_%E7%94%B7%E6%AC%BE%E5%A4%9C%E8%B7%91%E9%9E%8B_%E8%B6%85%E8%BD%BB%E9%80%8F%E6%B0%94%E8%B7%91%E9%9E%8B_%E6%85%A2%E8%B7%91%E9%9E%8B_%E8%B6%8A%E9%87%8E%E8%B7%91%E9%9E%8B-Under%20Armour%7C%E5%AE%89%E5%BE%B7%E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&pcp=1:Product%20List;2:ProductList%7C%E7%94%B7%E5%AD%90%E8%A3%85%E5%A4%87%7C%E7%94%B7%E5%AD%90%E9%9E%8B%E7%B1%BB%7C%E7%94%B7%E5%AD%90%E8%B7%91%E9%9E%8B" +
                "&gsce=1&gsclr=24&gsje=0&gsst=0&gswh=759&gsph=4764&gspw=1519&gssce=1&gsscr=1536*864&dedupid=291391249ebg6o17" +
                "&gsurl=https%3A%2F%2Fwww.underarmour.cn%2F01Grid_page_view%2Fcmens-footwear-running%2F%3Futm_source%3Dbaidu%26utm_campaign%3DPC%255F%25E6%25A0%25B8%25E5%25BF%2583%255F%25E9%2580%259A%25E7%2594%25A8%25E8%25AF%258D%255F%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%26utm_adgroup%3D%25E6%25A0%25B8%25E5%25BF%2583%255F%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%25EF%25BC%2588%25E7%259F%25AD%25E8%25AF%25AD%25EF%25BC%2589%26utm_term%3D%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%25E5%2593%2581%25E7%2589%258C%26utm_medium%3Dsearch%255Fcpc%26utm_channel%3DSEM" +
                "&gsref=https%3A%2F%2Fwww.baidu.com%2Fbaidu.php%3Fwd%3D%25E8%25BF%2590%25E5%258A%25A8%25E9%259E%258B%25E5%2593%2581%25E7%2589%258C%25E6%258E%2592%25E8%25A1%258C " +
                "80 - 58.210.35.226 Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";
        PreParsedLog preParsedLog = PreParserWebLog.parse(weblog);
        List<BaseDataObject> baseDataObjects = new PvDataObjectBuilder().doBuildDataObjects(preParsedLog);
        assertTrue(baseDataObjects.size() == 1);
        PvDataObject dataObject = (PvDataObject)baseDataObjects.get(0);
        //网站信息
        Assert.assertEquals("https://www.underarmour.cn/01Grid_page_view/cmens-footwear-running/?utm_source=baidu&utm_campaign=PC_核心_通用词_跑步鞋&utm_adgroup=核心_跑步鞋（短语）&utm_term=跑步鞋品牌&utm_medium=search_cpc&utm_channel=SEM", dataObject.getSiteResourceInfo().getUrl());
        Assert.assertEquals("www.underarmour.cn", dataObject.getSiteResourceInfo().getDomain());
        Assert.assertEquals("utm_source=baidu&utm_campaign=PC_核心_通用词_跑步鞋&utm_adgroup=核心_跑步鞋（短语）&utm_term=跑步鞋品牌&utm_medium=search_cpc&utm_channel=SEM", dataObject.getSiteResourceInfo().getQuery());
        Assert.assertEquals("UA男士跑步运动鞋_男款夜跑鞋_超轻透气跑鞋_慢跑鞋_越野跑鞋-Under Armour|安德玛中国官网", dataObject.getSiteResourceInfo().getPageTitle());
        Assert.assertEquals("-", dataObject.getSiteResourceInfo().getOriginalUrl());

        //广告信息
        Assert.assertEquals("核心_跑步鞋（短语）", dataObject.getAdInfo().getUtmAdGroup());
        Assert.assertEquals("SEM", dataObject.getAdInfo().getUtmChannel());
        Assert.assertEquals("PC_核心_通用词_跑步鞋", dataObject.getAdInfo().getUtmCampaign());
        Assert.assertEquals("-", dataObject.getAdInfo().getUtmContent());
        Assert.assertEquals("search_cpc", dataObject.getAdInfo().getUtmMedium());
        Assert.assertEquals("baidu", dataObject.getAdInfo().getUtmSource());
        Assert.assertEquals("跑步鞋品牌", dataObject.getAdInfo().getUtmTerm());

        //来源信息
        Assert.assertEquals("https://www.baidu.com/baidu.php?wd=运动鞋品牌排行", dataObject.getReferrerInfo().getUrl());
        Assert.assertEquals("www.baidu.com", dataObject.getReferrerInfo().getDomain());
        Assert.assertEquals("SEM", dataObject.getReferrerInfo().getChannel());
        Assert.assertEquals("-", dataObject.getReferrerInfo().getEqId());
        Assert.assertEquals("运动鞋品牌排行", dataObject.getReferrerInfo().getKeyword());
        Assert.assertEquals("wd=运动鞋品牌排行", dataObject.getReferrerInfo().getQuery());
        Assert.assertEquals("paid search", dataObject.getReferrerInfo().getReferType());
        Assert.assertEquals("baidu", dataObject.getReferrerInfo().getSearchEngineName());
        Assert.assertEquals("https://www.baidu.com/baidu.php", dataObject.getReferrerInfo().getUrlWithoutQuery());

        //浏览器信息
        Assert.assertEquals(false, dataObject.getBrowserInfo().isAlexaToolBar());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getBrowserLanguage());
        Assert.assertEquals("24", dataObject.getBrowserInfo().getColorDepth());
        Assert.assertEquals(true, dataObject.getBrowserInfo().isCookieEnable());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getDeviceName());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getDeviceType());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getFlashVersion());
        Assert.assertEquals(false, dataObject.getBrowserInfo().isJavaEnable());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getOsLanguage());
        Assert.assertEquals("1536*864", dataObject.getBrowserInfo().getResolution());
        Assert.assertEquals("-", dataObject.getBrowserInfo().getSilverlightVersion());
    }

    @Test
    public void testTargetPage() {
        String weblog = "2018-06-15 13:43:39 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=pv&gssrvid=GWD-000702&gsuid=29137384iqrvzn66&gssid=291373841jjmbp66" +
                "&pvid=29137480js20ou23&gsltime=1529166280514&gstmzone=8&rd=bpw47" +
                "&gstl=%E8%B4%AD%E7%89%A9%E8%BD%A6%20-%20Under%20Armour%20%E5%AE%98%E6%96%B9%E8%B4%AD%E7%89%A9%E7%BD%91%E7%AB%99" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&pcp=1:CheckLogin;2:CheckLogin%7C%E6%A3%80%E6%9F%A5%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2" +
                "&gsce=1&gsflver=30.0&gsclr=24&gsje=1&gsst=0&gswh=723&gsph=723&gspw=1536&gssce=1&gsscr=1536*864" +
                "&dedupid=291374809ldeyw23" +
                "&gsurl=https%3A%2F%2Fwww.underarmour.cn%2Fshopping%2FcheckoutLogin" +
                "&gsref=https%3A%2F%2Fwww.underarmour.cn%2FmyShoppingCart.htm 80 - 58.210.35.226 " +
                "Mozilla/5.0+(Windows+NT+10.0;+WOW64;+Trident/7.0;+LCTE;+rv:11.0)+like+Gecko";

        PreParsedLog preParsedLog = PreParserWebLog.parse(weblog);
        List<BaseDataObject> dataObjects = new PvDataObjectBuilder().doBuildDataObjects(preParsedLog);
        assertTrue(dataObjects.size() == 2);
        PvDataObject dataObject = (PvDataObject)dataObjects.get(0);
        TargetPageDataObject targetPageDataObject = (TargetPageDataObject)dataObjects.get(1);
        List<TargetPageInfo> targetInfos = targetPageDataObject.getTargetPagesInfos();
        Assert.assertEquals(1, targetInfos.size());

        TargetPageInfo targetInfo = targetInfos.get(0);
        Assert.assertEquals("57f8df278a4bf51e2d834be9", targetInfo.getKey());
        Assert.assertEquals("test target", targetInfo.getTargetName());
        Assert.assertEquals(true, targetInfo.isActive());
    }
}