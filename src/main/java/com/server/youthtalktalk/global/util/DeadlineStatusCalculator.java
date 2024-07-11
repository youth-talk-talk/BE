package com.server.youthtalktalk.global.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
public class DeadlineStatusCalculator {

    // 마감일 입력
    // 마감상태 반환 (D-00, D-DAY, 마감)

    public static String calculateDeadline(LocalDate applyDue) {
        if (applyDue == null) {
            return ""; // 상시
        }

        LocalDate today = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(today, applyDue);
        if (daysBetween > 0) {
            return "D-" + daysBetween;
        } else if (daysBetween == 0) {
            return "D-DAY";
        } else {
            return "마감";
        }
    }

}
