package com.zxl.parser.utils;

import java.util.HashMap;
import java.util.Map;

public class DateUtils {
    private static Map<Integer, String> week = new HashMap<>();

    static {
        week.put(1, "星期日");
        week.put(2, "星期一");
        week.put(3, "星期二");
        week.put(4, "星期三");
        week.put(5, "星期四");
        week.put(6, "星期五");
        week.put(7, "星期六");
    }

    public static String getChineseWeekStr(int i) {
        return week.get(i);
    }
}
