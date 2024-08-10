package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UrlUtil {

    public static String formatUrl(String url){

        // url 추출
        String urlPattern = "((http[s]?|ftp)://)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=가-힣]{1,256}[:|.][a-zA-Z0-9]{1,6}\\b(?:[-a-zA-Z0-9@:%_+,.~#?&/=가-힣]*)";
        Pattern pt = Pattern.compile(urlPattern);
        Matcher mc = pt.matcher(url.replaceAll("[()]", " "));

        return mc.find() ? mc.group() : "";

    }
}
