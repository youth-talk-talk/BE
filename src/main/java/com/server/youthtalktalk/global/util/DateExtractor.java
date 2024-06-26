package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DateExtractor {

    private final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}[.-]?\\d{2}[.-]?\\d{2})[~](\\d{4}[.-]?\\d{2}[.-]?\\d{2})");
    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
    };

    /**
     * 신청마감일 문자열 분리
     */
    public LocalDate extractDue(String dateStr) {
        if (dateStr.trim().equals("-")) return null;

        String[] parts = dateStr.split("~");
        if (parts.length < 2) return null;

        Matcher matcher = DATE_PATTERN.matcher(dateStr);
        if (matcher.find()) {
            String endDateStr = matcher.group(2).replace(".", "-");
            return parseDate(endDateStr);
        }
        log.info("No match found for={}", dateStr);
        return null;
    }

    private static LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                e.getStackTrace();
            }
        }
        return null;
    }

}
