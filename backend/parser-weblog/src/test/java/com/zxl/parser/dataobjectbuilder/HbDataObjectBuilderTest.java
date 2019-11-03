package com.zxl.parser.dataobjectbuilder;

import com.zxl.parser.dataobject.BaseDataObject;
import com.zxl.parser.dataobject.HbDataObject;
import com.zxl.preparser.PreParsedLog;
import com.zxl.preparser.PreParserWebLog;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class HbDataObjectBuilderTest {

    @Test
    public void doBuildDataObject() {
        String weblog = "2018-06-15 19:43:28 10.200.200.98 GET /gs.gif " +
                "gsver=3.7.0.14&gscmd=hb&gssrvid=GWD-002249&gsuid=288723943z1r7257&gssid=29139924x52ybq76" +
                "&pvid=2914025643u3cy61&gsltime=1529169209542&gstmzone=8&rd=fd450&plt=1.711&pld=0.317&psd=153.123" +
                "&gsst=0&gswh=759 80 - 58.210.35.226 " +
                "Mozilla/5.0+(Windows+NT+10.0;+Win64;+x64)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/67.0.3396.87+Safari/537.36";
        PreParsedLog preParsedLog = PreParserWebLog.parse(weblog);
        List<BaseDataObject> baseDataObjects = new HbDataObjectBuilder().doBuildDataObjects(preParsedLog);
        Assert.assertEquals(1, baseDataObjects.size());
        HbDataObject hbDataObject = (HbDataObject) baseDataObjects.get(0);
        Assert.assertEquals(2, hbDataObject.getLoadingDuration());
        Assert.assertEquals(153, hbDataObject.getClientPageDuration());
    }
}