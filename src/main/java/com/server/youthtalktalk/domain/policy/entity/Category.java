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
    JOB("001", "일자리"),
    DWELLING("002", "주거"),
    EDUCATION("003", "교육"),
    LIFE("004", "복지"),
    PARTICIPATION("005", "참여");

    private final String key;
    private final String name;

    public static Category fromKey(String policyNum, String key){
        return switch(key){
            case "001" -> Category.JOB;
            case "002" -> Category.DWELLING;
            case "003" -> Category.EDUCATION;
            case "004" -> Category.LIFE;
            case "005" -> Category.PARTICIPATION;
            default -> {
                log.error("[Policy Data] Not Existed Category = {}, policyNum = {}", key, policyNum);
                throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_CATEGORY);
            }
        };
    }
}
