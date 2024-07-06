package com.server.youthtalktalk.domain.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmploymentCode {
    EMPLOYED("재직자"),
    SELF_EMPLOYED("자영업자"),
    UNEMPLOYED("미취업자"),
    FREELANCER("프리랜서"),
    DAILY_WORKER("일용근로자"),
    PROSPECTIVE_ENTREPRENEUR("예비창업자"),
    TEMPORARY_WORKER("단기근로자"),
    FARMER("영농종사자"),
    NO_RESTRICTION("제한없음"),
    OTHER("기타");

    private final String name;

}
