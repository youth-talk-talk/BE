package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UrlUtil {

    public static String formatUrl(String url){

        // 이메일 주소 패턴
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        // url 추출 패턴
        String urlPattern = "((http[s]?|ftp)://)?(?:www\\.)?[-a-zA-Z0-9:%@._+~#=가-힣]{1,256}[:|.][a-zA-Z0-9]{1,6}\\b(?:[-a-zA-Z0-9@:%_+,.~#?&/=가-힣]*)";


        url = url.replaceAll(emailPattern, "");
        url = url.replaceAll("[()]", " ");

        Pattern pt = Pattern.compile(urlPattern);
        Matcher mc = pt.matcher(url);

        return mc.find() ? mc.group() : null;
    }
}