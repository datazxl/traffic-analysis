package com.zxl.parser.searchengine;

import java.util.Map;

public class ReferUrlAndParams {
    //去掉query的来源url
    private String referUrlWithoutQuery;
    //来源url中的query参数
    private Map<String, String> params;

    public ReferUrlAndParams(String referUrlWithoutQuery, Map<String, String> params) {
        this.referUrlWithoutQuery = referUrlWithoutQuery;
        this.params = params;
    }

    public String getReferUrlWithoutQuery() {
        return referUrlWithoutQuery;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
