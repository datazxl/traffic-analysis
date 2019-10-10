package com.zxl.parser.targetpage.service;

import com.zxl.parser.targetpage.TargetPage;
import com.zxl.parser.targetpage.loader.TargetPageConfigLoader;
import com.zxl.parser.targetpage.loader.impl.MongoTargetPageConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 目标页面匹配服务类
 */
public interface TargetPageConfigService {
    /**
     * pvurl和TargetPage匹配，返回匹配上的TargetPage(可能是多个)
     *
     * @param profileId
     * @param pvUrl
     * @return
     */
    public List<TargetPage> getMatchedTargetPages(int profileId, String pvUrl);
}