package com.server.youthtalktalk.global.util;

import com.server.youthtalktalk.domain.policy.entity.condition.Employment;

import java.util.EnumSet;
import java.util.Set;

public class EmploymentStatusClassifier {

    public static String classify(String employmentText) {

        if (employmentText == null)
            employmentText = "";
        String employment = employmentText.trim();

        Set<Employment> employmentCode = EnumSet.noneOf(Employment.class);
        if (employment.contains("재직자")) employmentCode.add(Employment.EMPLOYED);
        if (employment.contains("자영업자")) employmentCode.add(Employment.SELF_EMPLOYED);
        if (employment.contains("미취업")) employmentCode.add(Employment.UNEMPLOYED);
        if (employment.contains("프리랜서")) employmentCode.add(Employment.FREELANCER);
        if (employment.contains("일용근로자")) employmentCode.add(Employment.DAILY_WORKER);
        if (employment.contains("창업")) employmentCode.add(Employment.ENTREPRENEUR);
        if (employment.contains("단기근로자")) employmentCode.add(Employment.TEMPORARY_WORKER);
        if (employment.contains("영농종사자") || employment.contains("농업인")) employmentCode.add(Employment.FARMER);

        if (employmentCode.isEmpty()) {
            if (employment.equals("-") || employment.contains("제한없음") || employment.contains("상관없음") || employment.equals("무관")) {
                employmentCode.add(Employment.UNRESTRICTED);
            } else {
                employmentCode.add(Employment.OTHER);
            }
        }

        return employmentCode.toString();
    }
}
