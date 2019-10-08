package com.zxl.parser.utils;

public class QueryAndFragment {
    private String query;
    private String fragment;

    public QueryAndFragment(String query, String fragment) {
        this.query = query;
        this.fragment = fragment;
    }

    public String getQuery() {
        return query;
    }

    public String getFragment() {
        return fragment;
    }
}
