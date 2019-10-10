package com.zxl.parser;

import com.zxl.parser.dataobject.ParsedDataObject;
import com.zxl.parser.dataobjectbuilder.*;
import com.zxl.preparser.PreParsedLog;
import com.zxl.preparser.PreParserWebLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ParserLogTest {
    private ParserLog parserLog;

    @Before
    public void setUp() {
        Set<String> cmds = new HashSet<>();
        cmds.add("pv");
        cmds.add("mc");
        cmds.add("hb");
        cmds.add("ev");
        HashSet<AbstractDataObjectBuilder> dataObjectBuilders = new HashSet<>();

        McDataObjectBuilder mcDataObjectBuilder = new McDataObjectBuilder();
        HbDataObjectBuilder hbDataObjectBuilder = new HbDataObjectBuilder();
        EvDataObjectBuilder evDataObjectBuilder = new EvDataObjectBuilder();
        PvDataObjectBuilder pvDataObjectBuilder = new PvDataObjectBuilder();
        dataObjectBuilders.add(mcDataObjectBuilder);
        dataObjectBuilders.add(hbDataObjectBuilder);
        dataObjectBuilders.add(evDataObjectBuilder);
        dataObjectBuilders.add(pvDataObjectBuilder);
        parserLog = new ParserLog(cmds, dataObjectBuilders);
    }

    @Test
    public void parse() {
        List<PreParsedLog> preParsedLogs = new ArrayList<>();
        String hbStr = "2018-06-15 19:43:28 10.200.200.98 GET /gs.gif " +
                "gsver=3.7.0.14&gscmd=hb&gssrvid=GWD-002249&gsuid=288723943z1r7257&gssid=29139924x52ybq76" +
                "&pvid=2914025643u3cy61&gsltime=1529169209542&gstmzone=8&rd=fd450&plt=1.711&pld=0.317&psd=153.123" +
                "&gsst=0&gswh=759 80 - 58.210.35.226 " +
                "Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";
        preParsedLogs.add(PreParserWebLog.parse(hbStr));
        String evStr = "2018-06-15 13:43:39 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=ev&gssrvid=GWD-000702&gsuid=29137384iqrvzn66&gssid=291373841jjmbp66" +
                "&pvid=29137472grze7w28&gsltime=1529166280037&gstmzone=8&gsdelay=6130&rd=czuh8&eca=Checkout" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&gstl=%E8%B4%AD%E7%89%A9%E8%BD%A6%20-%20Under%20Armour%20%E5%AE%98%E6%96%B9%E8%B4%AD%E7%89%A9%E7%BD%91%E7%AB%99" +
                "&dedupid=29137480w2fo2j28&gsourl=https%3A%2F%2Fwww.underarmour.cn%2FmyShoppingCart.htm 80 - 58.210.35.226 " +
                "Mozilla/5.0+(Windows+NT+10.0;+WOW64;+Trident/7.0;+LCTE;+rv:11.0)+like+Gecko";
        preParsedLogs.add(PreParserWebLog.parse(evStr));
        String mcStr = "2018-06-15 13:41:50 10.200.200.98 GET /gs.gif " +
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
        preParsedLogs.add(PreParserWebLog.parse(mcStr));
        String pvStr = "2018-06-15 18:43:27 10.200.200.98 GET /gs.gif " +
                "gsver=3.8.0.9&gscmd=pv&gssrvid=GWD-000702&gsuid=28872593x9769t21&gssid=29138614b1vulq13" +
                "&pvid=29139124zhn5ps17&gsltime=1529167924411&gstmzone=8&rd=hz64j" +
                "&gstl=UA%E7%94%B7%E5%A3%AB%E8%B7%91%E6%AD%A5%E8%BF%90%E5%8A%A8%E9%9E%8B_%E7%94%B7%E6%AC%BE%E5%A4%9C%E8%B7%91%E9%9E%8B_%E8%B6%85%E8%BD%BB%E9%80%8F%E6%B0%94%E8%B7%91%E9%9E%8B_%E6%85%A2%E8%B7%91%E9%9E%8B_%E8%B6%8A%E9%87%8E%E8%B7%91%E9%9E%8B-Under%20Armour%7C%E5%AE%89%E5%BE%B7%E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&pcp=1:Product%20List;2:ProductList%7C%E7%94%B7%E5%AD%90%E8%A3%85%E5%A4%87%7C%E7%94%B7%E5%AD%90%E9%9E%8B%E7%B1%BB%7C%E7%94%B7%E5%AD%90%E8%B7%91%E9%9E%8B" +
                "&gsce=1&gsclr=24&gsje=0&gsst=0&gswh=759&gsph=4764&gspw=1519&gssce=1&gsscr=1536*864&dedupid=291391249ebg6o17" +
                "&gsurl=https%3A%2F%2Fwww.underarmour.cn%2F01Grid_page_view%2Fcmens-footwear-running%2F%3Futm_source%3Dbaidu%26utm_campaign%3DPC%255F%25E6%25A0%25B8%25E5%25BF%2583%255F%25E9%2580%259A%25E7%2594%25A8%25E8%25AF%258D%255F%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%26utm_adgroup%3D%25E6%25A0%25B8%25E5%25BF%2583%255F%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%25EF%25BC%2588%25E7%259F%25AD%25E8%25AF%25AD%25EF%25BC%2589%26utm_term%3D%25E8%25B7%2591%25E6%25AD%25A5%25E9%259E%258B%25E5%2593%2581%25E7%2589%258C%26utm_medium%3Dsearch%255Fcpc%26utm_channel%3DSEM" +
                "&gsref=https%3A%2F%2Fwww.baidu.com%2Fbaidu.php%3Fwd%3D%25E8%25BF%2590%25E5%258A%25A8%25E9%259E%258B%25E5%2593%2581%25E7%2589%258C%25E6%258E%2592%25E8%25A1%258C " +
                "80 - 58.210.35.226 Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";

        preParsedLogs.add(PreParserWebLog.parse(pvStr));
        List<? extends List<? extends ParsedDataObject>> parsedDataObjects = preParsedLogs.stream().map(preParsedLog -> {
            return parserLog.parse(preParsedLog);
        }).collect(Collectors.toList());

        System.out.println(parsedDataObjects.get(0));
        System.out.println(parsedDataObjects.get(1));
        System.out.println(parsedDataObjects.get(2));
        System.out.println(parsedDataObjects.get(3));
        Assert.assertEquals(4, parsedDataObjects.size());
    }
}