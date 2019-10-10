package com.zxl.parser.targetpage.loader.impl;

import com.zxl.parser.targetpage.TargetPage;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MongoTargetPageConfigLoaderTest {

    @Test
    public void loadAllTargetPagesConfig() {
        Map<Integer, List<TargetPage>> map = new MongoTargetPageConfigLoader().getTargetPagesConfigs();
        List<TargetPage> targetPages = map.get(702);
        List<TargetPage> specTargetPages = targetPages.stream().filter(targetPage -> targetPage.getId().equals("57f8df278a4bf51e2d834be9")
        ).collect(Collectors.toList());

        Assert.assertEquals(1, targetPages.size());
        TargetPage targetPage = specTargetPages.get(0);
        Assert.assertEquals("57f8df278a4bf51e2d834be9", targetPage.getId());
        Assert.assertEquals("test target", targetPage.getName());
        Assert.assertEquals("CONTAINS", targetPage.getMatchType());
        Assert.assertEquals(702, targetPage.getProfileId());
        Assert.assertEquals(true, targetPage.isEnable());
        Assert.assertEquals(false, targetPage.isMatchWithoutQueryString());
    }
}