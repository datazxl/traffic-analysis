package com.zxl.parser.utils;

import java.net.URLDecoder;

/**
 * 解析的一些工具方法
 */
public class ParseUtils {

    /**
     * 判断一个字符串是否为指定的空
     */
    public static boolean isNullOrEmptyOrDash(String str) {
        return str == null || str.trim().isEmpty() ||
                str.trim().equals("-") || str.trim().toLowerCase().equals("null");
    }

    /**
     * 将一个字符串转为非空字符串
     */
    public static String notNull(String str){
        if (isNullOrEmptyOrDash(str)){
            return "-";
        } else {
            return str;
        }
    }

    /**
     * 对一个已经编码的字符串进行解码
     * 有些字符串可能已经经过两次编码(比如url的参数等)，所以我们需要二次解码才能真正解码成功，例如：
     * https%3A%2F%2Fwww.underarmour.cn%2F%3Futm_source%3Dbaidu%26utm_term%3D%25E6%25A0%2587%25E9%25A2%2598%26utm_m
     * edium%3DBrandZonePC%26utm_channel%3DSEM
     * 第一次解码后为：
     * https://www.underarmour.cn/?utm_source=baidu&utm_term=%E6%A0%87%E9%A2%98&utm_medium=BrandZonePC&utm_channel=SEM
     * 第二次解码后为：
     * https://www.underarmour.cn/?utm_source=baidu&utm_term=标题&utm_medium=BrandZonePC&utm_channel=SEM
     *
     * @param encodedStr 编码后的字符串
     * @return 完全解码后的字符串
     */
    public static String decode(String encodedStr) {
        //1. 判空
        if (isNullOrEmptyOrDash(encodedStr)) {
            return encodedStr;
        }
        String decodedStr = "-";
        try {//2. 两次解码
            decodedStr = decodeTwice(encodedStr);
        } catch (Exception e) {//3. 解码失败，手动解码
            //有可能url被截断，导致编码后的url不完整，所以decode的报错如下错误：
            // java.lang.IllegalArgumentException: URLDecoder: Incomplete trailing escape (%) pattern
            //比如：https%3A%2F%2Fwww.underarmour.cn%2Fcmens-footwear-running%2F%3Futm_source%3Dbaidu%26utm_campaign%3DPC%2
            int lastPercentIndex = encodedStr.lastIndexOf("%");
            if (encodedStr.length() - lastPercentIndex == 2) { //说明URL最后一个参数不完整
                try {
                    decodedStr = decodeTwice(encodedStr.substring(0, lastPercentIndex));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return "-";
                }
            }
        }
        return decodedStr;
    }

    /**
     * 对字符串进行两次解码（如果是两次编码）
     *
     * @throws Exception 解码失败
     */
    private static String decodeTwice(String encodedStr) throws Exception {
        String decodedStr = URLDecoder.decode(encodedStr, "utf-8");
        //判断是否含有%,如果含有说明是进行了两次编码,因为有一些中文的url参数是两次编码的
        if (decodedStr.indexOf("%") > 0) {
            return URLDecoder.decode(decodedStr, "utf-8");
        }
        return decodedStr;
    }

    public static boolean parseBoolean(String number) {
        if ("1".equals(number)) {
            return true;
        } else {
            return  false;
        }
    }
}