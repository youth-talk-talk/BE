package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlFormatter {

    public static String fomatlUrl(String url){

        String fomattedUrl;

        // "http" 또는 "www"가 포함된 문자열에 대해 다음 공백 직전까지 추출
        fomattedUrl = extractUpToSpace(url, "http");
        fomattedUrl = extractUpToSpace(fomattedUrl, "www");

        // 한글, 공백, 괄호, 쉼표 제거
        fomattedUrl = fomattedUrl.replaceAll("[\\p{IsHangul}\\s(),]", "");

        // 영어 문자가 없는 경우, .이 없는 경우, @가 포함된 경우에 대해 null 처리 (@포함 여부로 이메일인지 링크인지 판단)
        if (!(fomattedUrl.matches(".*[a-zA-Z]+.*")) || !(fomattedUrl.contains(".")) || fomattedUrl.contains("@")) {
            return null;
        }

        // 링크가 없는 경우에 대해 null 처리
        if (fomattedUrl == null || fomattedUrl.isEmpty() || fomattedUrl.equals("-") || fomattedUrl.equals("null")) {
            return null;
        }

        return fomattedUrl;
    }

    private static String extractUpToSpace(String url, String str) {
        int startIndex = url.indexOf(str);
        if (startIndex == -1) {
            return url;
        }
        int endIndex = url.indexOf(' ', startIndex);
        return (endIndex != -1) ? url.substring(startIndex, endIndex) : url.substring(startIndex);
    }

}
