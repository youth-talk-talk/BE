package com.server.youthtalktalk.global.util;

import com.server.youthtalktalk.domain.policy.EmploymentCode;

import java.util.EnumSet;
import java.util.Set;

public class EmploymentStatusClassifier {

    public static String classify(String employmentText) {

        String employment = employmentText.trim();

        Set<EmploymentCode> employmentCode = EnumSet.noneOf(EmploymentCode.class);

        if (employment.contains("재직자")) employmentCode.add(EmploymentCode.EMPLOYED);
        if (employment.contains("자영업자")) employmentCode.add(EmploymentCode.SELF_EMPLOYED);
        if (employment.contains("미취업")) employmentCode.add(EmploymentCode.UNEMPLOYED);
        if (employment.contains("프리랜서")) employmentCode.add(EmploymentCode.FREELANCER);
        if (employment.contains("일용근로자")) employmentCode.add(EmploymentCode.DAILY_WORKER);
        if (employment.contains("창업")) employmentCode.add(EmploymentCode.ENTREPRENEUR);
        if (employment.contains("단기근로자")) employmentCode.add(EmploymentCode.TEMPORARY_WORKER);
        if (employment.contains("영농종사자") || employment.contains("농업인")) employmentCode.add(EmploymentCode.FARMER);

        if (employmentCode.isEmpty()) {
            if (employment.equals("-") || employment.contains("제한없음") || employment.contains("상관없음") || employment.equals("무관")) {
                employmentCode.add(EmploymentCode.NO_RESTRICTION);
            } else {
                employmentCode.add(EmploymentCode.OTHER);
            }
        }

        return employmentCode.toString();
    }
}
