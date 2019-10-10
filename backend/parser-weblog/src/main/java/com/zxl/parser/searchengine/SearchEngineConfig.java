package com.zxl.parser.searchengine;

import java.util.regex.Pattern;

/**
 * 搜索引擎配置实体类
 * 采用正则匹配来匹配来源url和搜索引擎配置的正则url
 */
public class SearchEngineConfig {
    private int keyId;
    private String searchEngineName;
    private String regexUrl;
    private String searchKeywordKey;


    public SearchEngineConfig(int keyId, String searchEngineName, String regexUrl, String searchKeywordKey) {
        this.keyId = keyId;
        this.searchEngineName = searchEngineName;
        this.regexUrl = regexUrl;
        this.searchKeywordKey = searchKeywordKey;
    }

    public String getSearchEngineName() {
        return searchEngineName;
    }

    public String getSearchKeywordKey() {
        return searchKeywordKey;
    }

    public int getKeyId() {
        return keyId;
    }

    /**
     * 根据来源url和来源url中的参数和当前的这个搜索引擎配置进行匹配
     * 判断出来源是否是属于当前这个搜索引擎
     *
     * @param referUrlAndParams
     * @return 如果属于当前的搜索引擎则返回true, 否则返回false
     */
    public boolean match(ReferUrlAndParams referUrlAndParams) {
        //来源url和当前搜索引擎的regexUrl进行正则匹配
        String urlWithoutQuery = referUrlAndParams.getReferUrlWithoutQuery();
        Pattern pattern = Pattern.compile(regexUrl);
        if (pattern.matcher(urlWithoutQuery).find()) {
            //如果当前搜索引擎的关键词key不为空的话，则需要看看来源url中的query参数中是否含有这个key
            if (searchKeywordKey != null && !"null".equals(searchKeywordKey)) {
                return referUrlAndParams.getParams().containsKey(searchKeywordKey);
            } else {
                return true;
            }
        }
        return false;
    }
}
