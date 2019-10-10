package com.zxl.parser.searchengine.service;

import com.zxl.parser.searchengine.ReferUrlAndParams;
import com.zxl.parser.searchengine.SearchEngineConfig;
import com.zxl.parser.searchengine.loader.SearchEngineConfigLoader;
import com.zxl.parser.searchengine.loader.impl.FileSearchEngineConfigLoader;

import java.util.List;


/**
 *  搜索引擎配置服务类
 */
public interface SearchEngineConfigService {
    /**
     *  根据来源url匹配已经配置好的所有的搜索引擎的配置
     *  找到第一个匹配到的搜索引擎并返回
     * @param referUrlAndParams
     * @return 匹配到的搜索引擎，如果没有匹配的搜索引擎的话则返回null
     */
    public SearchEngineConfig doMatch(ReferUrlAndParams referUrlAndParams);
}