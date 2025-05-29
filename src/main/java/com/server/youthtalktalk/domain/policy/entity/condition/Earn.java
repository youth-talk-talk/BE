package com.server.youthtalktalk.domain.policy.entity.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Earn {
    UNRESTRICTED("0043001", "제한없음"),
    ANNUL_INCOME("0043002", "연소득"),
    OTHER("0043003", "기타");

    private final String key;
    private final String name;

    public static Earn fromKey(String policyId, String key){
        if(key == null) return Earn.UNRESTRICTED;

        return switch (key) {
            case "0043001" -> Earn.UNRESTRICTED;
            case "0043002" -> Earn.ANNUL_INCOME;
            case "0043003" -> Earn.OTHER;
            default -> Earn.UNRESTRICTED; // 기본값은 제한 없음
        };
    }
}
