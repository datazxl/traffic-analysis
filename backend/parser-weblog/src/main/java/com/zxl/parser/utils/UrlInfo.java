package com.zxl.parser.utils;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;
import static com.zxl.parser.utils.ParseUtils.notNull;

/**
 * 封装Url信息
 * rawUrl -> https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#NewLaunch
 * scheme -> https
 * hostport(domain) -> www.underarmour.cn
 * path -> /s-HOVR
 * query -> qf=11-149&pf=&sortStr=&nav=640
 * fragment -> NewLaunch
 */
public class UrlInfo {
    private String rawUrl;
    private String scheme;
    private String hostport;
    private String path;
    private String query;
    private String fragment;

    public UrlInfo(String rawUrl, String scheme, String hostport, String path, String query, String fragment) {
        this.rawUrl = rawUrl;
        this.scheme = scheme;
        this.hostport = hostport;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    public String getPathQueryFragment() {
        //判断Query和Fragment是否为空，返回拼接后的字符串
        if (!isNullOrEmptyOrDash(query) && !isNullOrEmptyOrDash(fragment)) {
            return path + "?" + query + "#" + fragment;
        } else if (isNullOrEmptyOrDash(query) && !isNullOrEmptyOrDash(fragment)) {
            return path + "#" + fragment;
        } else if (!isNullOrEmptyOrDash(query) && isNullOrEmptyOrDash(fragment)) {
            return path + "?" + query;
        } else {
            return notNull(path);
        }
    }

    public String getUrlWithoutQuery() {
        //判断是否存在Path
        if (isNullOrEmptyOrDash(path)) {
            return scheme + "://" + hostport;
        } else {
            return scheme + "://" + hostport + path;
        }
    }

    public String getFullUrl() {
        if (isNullOrEmptyOrDash(rawUrl)) {
            return "-";
        } else {
            return rawUrl;
        }
    }

    public String getDomain() {
        if (isNullOrEmptyOrDash(hostport)) {
            return "-";
        } else {
            return hostport;
        }
    }

    public String getScheme() {
        return scheme;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getFragment() {
        return fragment;
    }
}