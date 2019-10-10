package com.zxl.parser.iplocation;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserIpLocationTest {

    @Test
    public void parse() {
        IpLocation ipLocation = ParserIpLocation.parse("58.210.35.226");
        Assert.assertNotNull(ipLocation);
        Assert.assertEquals("CN", ipLocation.getCountry());
        Assert.assertEquals("Jiangsu", ipLocation.getRegion());
        Assert.assertEquals("Suzhou", ipLocation.getCity());
        Assert.assertEquals("31.3041", ipLocation.getLatitude());
        Assert.assertEquals("120.5954", ipLocation.getLongitude());
    }
}