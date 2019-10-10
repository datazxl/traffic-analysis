package com.zxl.parser.searchengine.loader.impl;


import com.zxl.parser.searchengine.SearchEngineConfig;
import com.zxl.parser.searchengine.loader.SearchEngineConfigLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

/**
 * 搜索引擎配置的加载的实现类
 * 从配置文件中加载搜索引擎的配置
 */
public class FileSearchEngineConfigLoader implements SearchEngineConfigLoader {

    private List<SearchEngineConfig> searchEngineConfigs = new ArrayList();

    public FileSearchEngineConfigLoader() {
        //类构造时将所有的搜索引擎配置加载并且解析好，放在内存中
        //读取配置文件searchEngine.conf
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("searchEngine.conf");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            Properties properties = new Properties();
            properties.load(reader);
            properties.forEach(new BiConsumer<Object, Object>() {
                @Override
                public void accept(Object key, Object value) {
                    //将配置文件中的每一行配置转换成搜索引擎配置，且将搜索引擎配置放到缓存列表中
                    String strValue = (String) value;
                    String newValue = strValue.replace("\"", "");
                    String[] temps = newValue.split(",");
                    SearchEngineConfig searchEngineConfig =
                            new SearchEngineConfig(Integer.parseInt(key.toString()), temps[0], temps[1], temps[2]);
                    searchEngineConfigs.add(searchEngineConfig);
                }
            });
            //对搜索引擎配置列表按照keyId进行升序排序
            searchEngineConfigs.sort(new Comparator<SearchEngineConfig>() {
                @Override
                public int compare(SearchEngineConfig o1, SearchEngineConfig o2) {
                    return o1.getKeyId() - o2.getKeyId();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<SearchEngineConfig> getSearchEngineConfigs() {
        return searchEngineConfigs;
    }
}