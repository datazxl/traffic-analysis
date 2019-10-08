package com.zxl.parser.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.zxl.parser.utils.ParseUtils.isNullOrEmptyOrDash;

public class UrlParseUtils {
    /**
     * 解析字符串的url为UrlInfo
     * 正常规则的url：
     * https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#NewLaunch
     * https://www.underarmour.cn/s-HOVR#NewLaunch?qf=11-149&pf=&sortStr=&nav=640
     * @param url
     * @return
     */
    public static UrlInfo getInfoFromUrl(String url) {
        String trimedUrl = url.trim();
        //1.判空
        if (isNullOrEmptyOrDash(trimedUrl)) {
            return new UrlInfo("-", "-", "-", "-", "-", "-");
        }

        int firstQuestionMarkIndex = trimedUrl.indexOf("?");
        int firstPoundMarkIndex = trimedUrl.indexOf("#");

        try {//2.使用URI解析标准的Url。
            URI uri = new URI(url).normalize();

            int port = uri.getPort();
            String hostport;
            //port是否存在
            if (port == -1) {
                hostport = uri.getHost();
            } else {
                hostport = uri.getHost() + ":" + uri.getPort();
            }

            String query;
            String fragment;
            //query、fragment前后顺序可能不一样
            if (firstPoundMarkIndex > 0 && firstQuestionMarkIndex > firstPoundMarkIndex) { //fragment在query前
                query = trimedUrl.substring(firstQuestionMarkIndex + 1);
                fragment = trimedUrl.substring(firstPoundMarkIndex + 1, firstQuestionMarkIndex);
            } else { //fragment在query后
                query = uri.getQuery();
                fragment = uri.getFragment();
            }
            return new UrlInfo(trimedUrl, uri.getScheme(), hostport, uri.getRawPath(), query, fragment);
        } catch (URISyntaxException e) {//3.解析非标准的Url：使用字符串切割
            try {
                //判断有无query参数
                if (firstQuestionMarkIndex == -1) {//没有query参数
                    return parseUrlWithoutQuery(trimedUrl, firstPoundMarkIndex);
                } else {//有query参数
                    return parseUrlWithQuery(trimedUrl, firstQuestionMarkIndex, firstPoundMarkIndex);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return new UrlInfo("-", "-", "-", "-", "-", "-");
            }
        }
    }

    /**
     * 解析不规则但是含有query的url, 例如：
     * https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607
     * 或者：
     * https://www.underarmour.cn/s-HOVR#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607?qf=11-149&pf=&sortStr=&nav=640
     *
     * @param url
     * @param firstQuestionMarkIndex 第一个 ? 号所在的位置
     * @param firstPoundMarkIndex    第一个 # 号所在的位置
     * @return
     */
    private static UrlInfo parseUrlWithQuery(String url, int firstQuestionMarkIndex, int firstPoundMarkIndex) {
        QueryAndFragment queryAndFragment = getQueryAndFragment(url, firstQuestionMarkIndex, firstPoundMarkIndex);
        int colonIndex = url.indexOf(":");
        String scheme = url.substring(0, colonIndex);
        String hostport = url.substring(colonIndex + 3, url.indexOf("/", colonIndex + 3));
        String path = "-";
        if (firstPoundMarkIndex > 0 && firstPoundMarkIndex > firstQuestionMarkIndex) {
            path = url.substring(url.indexOf("/", colonIndex + 3), firstQuestionMarkIndex);
        } else if (firstPoundMarkIndex > 0) {
            path = url.substring(url.indexOf("/", colonIndex + 3), firstPoundMarkIndex);
        } else {
            path = url.substring(url.indexOf("/", colonIndex + 3), firstQuestionMarkIndex);
        }
        return new UrlInfo(url, scheme, hostport,
                path, queryAndFragment.getQuery(), queryAndFragment.getFragment());
    }

    /**
     * 获取url的query和fragment
     * query和fragment的前后顺序不确定， 例如：
     * https://www.underarmour.cn/s-HOVR?qf=11-149&pf=&sortStr=&nav=640#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607
     * 或者：
     * https://www.underarmour.cn/s-HOVR#44-1|NewLaunch|HOVR|HOVR|HOVR|201800607?qf=11-149&pf=&sortStr=&nav=640
     *
     * @param url
     * @param firstQuestionMarkIndex 第一个 ? 号所在的位置
     * @param firstPoundMarkIndex    第一个 # 号所在的位置
     * @return
     */
    private static QueryAndFragment getQueryAndFragment(String url, int firstQuestionMarkIndex, int firstPoundMarkIndex) {
        if (firstPoundMarkIndex > 0) {
            if (firstQuestionMarkIndex > firstPoundMarkIndex) {
                return new QueryAndFragment(url.substring(firstQuestionMarkIndex + 1), url.substring(firstPoundMarkIndex + 1, firstQuestionMarkIndex));
            } else {
                return new QueryAndFragment(url.substring(firstQuestionMarkIndex + 1, firstPoundMarkIndex), url.substring(firstPoundMarkIndex + 1));
            }
        } else {
            return new QueryAndFragment(url.substring(firstQuestionMarkIndex + 1), "");
        }
    }

    /**
     * 解析不规则且没有query的url，例如：
     * https://www.underarmour.cn/cmens-tops-shortsleeve/#11|Mens|Tops|Shortsleeve|2-MensCategory-MensCategory
     *
     * @param trimedUrl
     * @param firstPoundMarkIndex 第一个 # 符号的位置
     * @return
     */
    private static UrlInfo parseUrlWithoutQuery(String trimedUrl, int firstPoundMarkIndex) {

        int colonIndex = trimedUrl.indexOf(":");
        String scheme = trimedUrl.substring(0, colonIndex);
        String hostport = trimedUrl.substring(colonIndex + 3, trimedUrl.indexOf("/", colonIndex + 3));

        String path;
        //判断有无fragment
        if (firstPoundMarkIndex == -1) {
            path = trimedUrl.substring(trimedUrl.indexOf("/", colonIndex + 3));
        } else {
            path = trimedUrl.substring(trimedUrl.indexOf("/", colonIndex + 3), firstPoundMarkIndex);
        }
        String fragment = "-";
        if (firstPoundMarkIndex > 0) fragment = trimedUrl.substring(firstPoundMarkIndex + 1);
        return new UrlInfo(trimedUrl, scheme, hostport, path, "-", fragment);
    }


    /**
     * 从url中的query中截取每一个参数，例如：
     * query为：qf=11-149&pf=&sortStr=&nav=640
     * 则返回：
     * Map(qf -> 11-149, pf -> "-", sortStr -> "-", nav -> 640)
     *
     * @param query
     * @return 参数键值对
     */
    public static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new HashMap();
        if (isNullOrEmptyOrDash(query)) {
            return params;
        }
        String[] temps = query.split("&");
        for (String str : temps) {
            String[] kv = str.split("=");
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                params.put(kv[0], "-");
            }
        }
        return params;
    }
}