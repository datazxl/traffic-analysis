package com.zxl.parser.targetpage.service;

import com.zxl.parser.targetpage.TargetPage;
import com.zxl.parser.targetpage.service.impl.MongoTargetPageConfigService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TargetPageConfigServiceTest {

    @Test
    public void getMatchedTargetPages() {
        List<TargetPage> targetPages = MongoTargetPageConfigService.getInstance().getMatchedTargetPages(702, "http://temp.com/checkoutLogin");
        Assert.assertEquals(1, targetPages.size());
        Assert.assertEquals("/checkoutLogin", targetPages.get(0).getMatchPattern());
    }
}