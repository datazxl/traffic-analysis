package com.zxl.parser.utils;

import org.junit.Assert;
import org.junit.Test;

public class ColumnReaderTest {

    @Test
    public void getStringValue() {
        String line = "gsver=3.8.0.9&gscmd=pv&gssrvid=GWD-000702&gsuid=28872593x9769t21&gssid=t291319151wwp6d11" +
                "&pvid=29135550ivt92z20&gsltime=1529164350604&gstmzone=8&rd=xq1jq" +
                "&gstl=UA%E5%AE%98%E7%BD%91%E7%83%AD%E5%8D%96%E8%BF%90%E5%8A%A8%E8%A3%85%E5%A4%87%EF%BC%8C%E7%95%85%E9%94%80%E8%BF%90%E5%8A%A8%E9%9E%8B%E3%80%81%E8%BF%90%E5%8A%A8%E8%A3%85%E3%80%81%E8%BF%90%E5%8A%A8%E6%9C%8D%E6%8E%A8%E8%8D%90%20-%20Under%20Armour%7C%E5%AE%89%E5%BE%B7%E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91" +
                "&gscp=2%3A%3Acookie%2520not%2520exist.%7C%7C3%3A%3Acookie%2520not%2520exist.%7C%7C4%3A%3Acookie%2520not%2520exist.%7C%7C5%3A%3Acookie%2520not%2520exist.%7C%7C6%3A%3Acookie%2520not%2520exist." +
                "&pcp=1:Product%20List;2:ProductList%7C%E7%83%AD%E5%8D%96%E5%95%86%E5%93%81" +
                "&gsce=1&gsclr=24&gsje=0&gsst=0&gswh=759&gsph=12292&gspw=1519&gssce=1&gsscr=1536*864" +
                "&dedupid=29135550rb6crr20" +
                "&gsurl=https%3A%2F%2Fwww.underarmour.cn%2F01Grid_page_view%2Fcvirtual-bestsell%2F%2311" +
                "&gsref=https%3A%2F%2Fwww.underarmour.cn%2Fcvirtual-newitem-womannewitem%2F";
        ColumnReader columnReader = new ColumnReader(line);
        Assert.assertEquals("3.8.0.9", columnReader.getStringValue("gsver"));
        Assert.assertEquals("1:Product List;2:ProductList|热卖商品", columnReader.getStringValue("pcp"));
    }
}