package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
public class DeadlineStatusCalculator {

    // 마감일 입력
    // 마감상태 반환 (상시, D-day, 마감)

    public static String calculateDeadline(LocalDate applyDue) {
        if (applyDue == null) {
            return "상시";
        }

        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, applyDue);
        if (daysBetween < 0) {
            return "마감";
        } else {
            return "D-" + daysBetween;
        }
    }

}
