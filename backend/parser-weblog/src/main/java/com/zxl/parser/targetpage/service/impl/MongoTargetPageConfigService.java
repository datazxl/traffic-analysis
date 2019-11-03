package com.zxl.parser.targetpage.service.impl;

import com.zxl.parser.targetpage.TargetPage;
import com.zxl.parser.targetpage.loader.TargetPageConfigLoader;
import com.zxl.parser.targetpage.loader.impl.MongoTargetPageConfigLoader;
import com.zxl.parser.targetpage.service.TargetPageConfigService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 目标页面匹配服务类
 */
public class MongoTargetPageConfigService implements TargetPageConfigService{

    private static MongoTargetPageConfigService targetPageConfigService = new MongoTargetPageConfigService();
    private TargetPageConfigLoader loader = new MongoTargetPageConfigLoader();
    private Map<Integer, List<TargetPage>> profileId2TargetPages = loader.getTargetPagesConfigs();
    /**
     * 对构造子私有化
     * 从而达到利用单例模式
     */
    private MongoTargetPageConfigService() {
    }

    public static MongoTargetPageConfigService getInstance() {
        return targetPageConfigService;
    }

    /**
     * pvurl和TargetPage匹配，返回匹配上的TargetPage(可能是多个)
     * @param profileId
     * @param pvUrl
     * @return
     */
    public List<TargetPage> getMatchedTargetPages(int profileId, String pvUrl) {
        List<TargetPage> targetPages = profileId2TargetPages.getOrDefault(profileId, new ArrayList<TargetPage>());
        List<TargetPage> matchedTargetPages = new ArrayList<>();

        // 如果目标页面和来源url匹配的上，就将该目标页面TargetPage放入list
        for (TargetPage targetPage : targetPages) {
            if (targetPage.match(pvUrl) == true) {
                matchedTargetPages.add(targetPage);
            }
        }
        return matchedTargetPages;
    }

}