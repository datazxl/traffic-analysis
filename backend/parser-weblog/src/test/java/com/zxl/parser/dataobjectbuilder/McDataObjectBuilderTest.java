package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.HbDataObject;
import com.zxl.parser.dataobject.McDataObject;
import com.zxl.preparser.PreParsedLog;
import com.zxl.preparser.PreParserWebLog;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class McDataObjectBuilderTest {
    @Test
    public void doBuildDataObject() {
        String weblog = "2018-06-15 13:41:50 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=mc&gssrvid=GWD-000702&gsuid=28872593x9769t21" +
                "&gssid=t291319151wwp6d11&pvid=29135535s1ijti21&gsltime=1529164349064" +
                "&gstmzone=8&rd=kuu36&btn=0&lt=%E7%83%AD%E5%8D%96%E5%95%86%E5%93%81" +
                "&lx=208&ly=34&lw=80&lh=60&ubtype=click&tgcg=selector" +
                "&tgpth=div%23wrapper%7B27%7D%3Eheader.header.events-header%7B2%7D%3Ediv.header-container.container-width.float-clearfix%7B1%7D%3Enav.nav-bar.header-left%7B1%7D%3Ediv.navbar-menu.inline-block.events-navbar-menu%7B2%7D%3Ediv.scroller-wrap%7B1%7D%3Ediv.menu-box.header-menu-content%7B1%7D%3Eul.nav.nav-pills.events-menu-sub.events-sub-menu-ul.float-clearfix%7B2%7D%3Eli.is-active%7B2%7D%3Ea" +
                "&tgtag=a&tgtxt=%E7%83%AD%E5%8D%96%E5%95%86%E5%93%81&tgidx=1" +
                "&tghre=https%3A%2F%2Fwww.underarmour.cn%2Fcvirtual-bestsell%2F%2311&ubid=291355495v6mmw21" +
                "&gspver=ver20180126&gsmcoffsetx=265&gsmcoffsety=67&gselmw=80&gselmh=60&gsmcelmx=57&gsmcelmy=33" +
                "&gstl=UA%E5%A5%B3%E5%AD%90%E6%96%B0%E5%93%81%E6%8E%A8%E8%8D%90%E5%95%86%E5%93%81%20-%20Under%20Armour%7C%E5%AE%89%E5%BE%B7%E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91" +
                "&gssn=0&gsscr=1536*864&gsorurl=https%3A%2F%2Fwww.underarmour.cn%2Fcvirtual-newitem-mennewitem%2F%2322%7CGsNewArrival" +
                "&gsmcurl=https%3A%2F%2Fwww.underarmour.cn%2Fcvirtual-newitem-womannewitem%2F%2322%7C2%7CWsNewArrival" +
                "&lk=https%3A%2F%2Fwww.underarmour.cn%2Fcvirtual-bestsell%2F%2311 " +
                "80 - 58.210.35.226 Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";

        PreParsedLog preParsedLog = PreParserWebLog.parse(weblog);

        List<BaseDataObject> baseDataObjects = new McDataObjectBuilder().doBuildDataObjects(preParsedLog);
        Assert.assertEquals(1, baseDataObjects.size());
        McDataObject dataObject = (McDataObject) baseDataObjects.get(0);
        //通用字段
        Assert.assertEquals("mc", dataObject.getCommand());
        Assert.assertEquals("29135535s1ijti21", dataObject.getPvId());
        Assert.assertEquals("2018-06-15 13:41:50", dataObject.getServerTimeString());
        Assert.assertEquals("3.8.0.9", dataObject.getTrackerVersion());
        Assert.assertEquals("28872593x9769t21", dataObject.getUserId());
        Assert.assertEquals(702, dataObject.getProfileId());

        Assert.assertEquals("58.210.35.226", dataObject.getClientIp());
        Assert.assertEquals("CN", dataObject.getIpLocation().getCountry());
        Assert.assertEquals("Jiangsu", dataObject.getIpLocation().getRegion());
        Assert.assertEquals("Suzhou", dataObject.getIpLocation().getCity());

        Assert.assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36", dataObject.getUserAgent());
        Assert.assertEquals("Chrome", dataObject.getUserAgentInfo().getBrowser().getName());
        Assert.assertEquals("WEB_BROWSER", dataObject.getUserAgentInfo().getBrowser().getBrowserType().toString());
        Assert.assertEquals("67.0.3396.87", dataObject.getUserAgentInfo().getBrowserVersion().getVersion());
        Assert.assertEquals("Windows 10", dataObject.getUserAgentInfo().getOperatingSystem().getName());
        Assert.assertEquals("Computer", dataObject.getUserAgentInfo().getOperatingSystem().getDeviceType().getName());

        //鼠标点击字段
        Assert.assertEquals("热卖商品", dataObject.getLinkText());
        Assert.assertEquals("https://www.underarmour.cn/cvirtual-bestsell/#11", dataObject.getLinkUrl());
        Assert.assertEquals("www.underarmour.cn", dataObject.getLinkHostName());
        Assert.assertEquals(208, dataObject.getLinkX());
        Assert.assertEquals(34, dataObject.getLinkY());
        Assert.assertEquals("https://www.underarmour.cn/cvirtual-newitem-mennewitem/#22|GsNewArrival", dataObject.getOriginalUrl());
        Assert.assertEquals(0, dataObject.getPageRegion());
        Assert.assertEquals("UA女子新品推荐商品 - Under Armour|安德玛中国官网", dataObject.getPageTitle());
        Assert.assertEquals("-", dataObject.getPageVersion());
        Assert.assertEquals(0, dataObject.getSnapshotId());
        Assert.assertEquals("https://www.underarmour.cn/cvirtual-newitem-womannewitem/#22|2|WsNewArrival", dataObject.getUrl());
        Assert.assertEquals("www.underarmour.cn", dataObject.getPageHostName());
        Assert.assertEquals(265, dataObject.getClickX());
        Assert.assertEquals(67, dataObject.getClickY());
        Assert.assertEquals(80, dataObject.getLinkWidth());
        Assert.assertEquals(60, dataObject.getLinkHeight());
        Assert.assertEquals("1536*864", dataObject.getClickScreenResolution());
        Assert.assertEquals(true, dataObject.isLinkClicked());
    }
}