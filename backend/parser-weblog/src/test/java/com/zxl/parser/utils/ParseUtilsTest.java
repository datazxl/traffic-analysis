package com.zxl.parser.utils;

import org.junit.Assert;
import org.junit.Test;

public class ParseUtilsTest {

    @Test
    public void decode() {
        Assert.assertEquals("https://www.underarmour.cn/",
                ParseUtils.decode("https%3A%2F%2Fwww.underarmour.cn%2F"));
        Assert.assertEquals("Under Armour|安德玛中国官网 - UA运动品牌专卖，美国高端运动科技品牌",
                ParseUtils.decode("Under%20Armour%7C%E5%AE%89%E5%BE%B7%E7%8E%9B%E4%B8%AD%E5%9B%BD%E5%AE%98%E7%BD%91%20-%20UA%E8%BF%90%E5%8A%A8%E5%93%81%E7%89%8C%E4%B8%93%E5%8D%96%EF%BC%8C%E7%BE%8E%E5%9B%BD%E9%AB%98%E7%AB%AF%E8%BF%90%E5%8A%A8%E7%A7%91%E6%8A%80%E5%93%81%E7%89%8C"));
        //需要二次解码
        Assert.assertEquals("https://www.underarmour.cn/?utm_source=baidu&utm_term=标题&utm_medium=BrandZonePC&utm_channel=SEM",
                ParseUtils.decode("https%3A%2F%2Fwww.underarmour.cn%2F%3Futm_source%3Dbaidu%26utm_term%3D%25E6%25A0%2587%25E9%25A2%2598%26utm_medium%3DBrandZonePC%26utm_channel%3DSEM"));
        //编码后不完整的
        Assert.assertEquals("https://www.underarmour.cn/cmens-footwear-running/?utm_source=baidu&utm_campaign=PC",
                ParseUtils.decode("https%3A%2F%2Fwww.underarmour.cn%2Fcmens-footwear-running%2F%3Futm_source%3Dbaidu%26utm_campaign%3DPC%2"));
    }

}