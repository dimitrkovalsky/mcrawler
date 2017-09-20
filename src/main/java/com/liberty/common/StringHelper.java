package com.liberty.common;

import org.springframework.util.StringUtils;

public class StringHelper {
    public static String cleanString(String toClean) {
        if (StringUtils.isEmpty(toClean)) {
            return "";
        }
        return toClean.toLowerCase().replaceAll("[^\\p{L}\\p{Z}]", "").trim();
    }
}
