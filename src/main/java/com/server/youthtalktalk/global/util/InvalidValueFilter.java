package com.server.youthtalktalk.global.util;
public class InvalidValueFilter {
    public static String filterInvalidValue(String origin) {
        String value = origin.replace(" ", "");
        if (value == null || value.equals("-") || value.equalsIgnoreCase("null")
                || value.equals("해당없음") || value.equals("제한없음") || value.equals("상관없음") || value.equals("없음")
                || value.equals("무관") || value.equals("□제한없음") || value.equals("-제한없음")) {
            return "";
        }
        return origin;
    }
}
