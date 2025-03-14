package com.server.youthtalktalk.domain.policy.entity;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.policy.FailPolicyDataException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum Category {
    JOB("일자리"),
    DWELLING("주거"),
    EDUCATION("교육"),
    LIFE("생활지원"),
    PARTICIPATION("참여");

    private final String key;

    public static Category fromKey(String policyId, String key){
        return switch(key){
            case "001" -> Category.JOB;
            case "002" -> Category.DWELLING;
            case "003" -> Category.EDUCATION;
            case "004" -> Category.LIFE;
            case "005" -> Category.PARTICIPATION;
            default -> {
                log.error("[Policy Data] Not Existed Category = {}, policyId = {}", key, policyId);
                throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_CATEGORY);
            }
        };
    }
}
