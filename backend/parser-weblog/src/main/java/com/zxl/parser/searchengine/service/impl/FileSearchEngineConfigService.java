package com.zxl.parser.searchengine.service.impl;

import com.zxl.parser.searchengine.ReferUrlAndParams;
import com.zxl.parser.searchengine.SearchEngineConfig;
import com.zxl.parser.searchengine.loader.SearchEngineConfigLoader;
import com.zxl.parser.searchengine.loader.impl.FileSearchEngineConfigLoader;
import com.zxl.parser.searchengine.service.SearchEngineConfigService;

import java.util.List;


/**
 *  搜索引擎配置服务类
 */
public class FileSearchEngineConfigService implements SearchEngineConfigService{
    private static FileSearchEngineConfigService searchEngineConfigService = new FileSearchEngineConfigService();
    private SearchEngineConfigLoader loader = new FileSearchEngineConfigLoader();
    private List<SearchEngineConfig> searchEngineConfigs = loader.getSearchEngineConfigs();

    /**
     * 对构造子私有化
     * 从而达到利用单例模式
     */
    private FileSearchEngineConfigService() {}

    public static FileSearchEngineConfigService getInstance() {
        return searchEngineConfigService;
    }

    /**
     *  根据来源url匹配已经配置好的所有的搜索引擎的配置
     *  找到第一个匹配到的搜索引擎并返回
     * @param referUrlAndParams
     * @return 匹配到的搜索引擎，如果没有匹配的搜索引擎的话则返回null
     */
    public SearchEngineConfig doMatch(ReferUrlAndParams referUrlAndParams) {
        for (SearchEngineConfig searchEngineConfig : searchEngineConfigs) {
            if (searchEngineConfig.match(referUrlAndParams)) {
                return searchEngineConfig;
            }
        }
        return null;
    }
}