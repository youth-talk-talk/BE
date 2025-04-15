package com.server.youthtalktalk.domain.policy.entity.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Marriage {
    MARRIED("0055001","기혼"),
    SINGLE("0055002","미혼"),
    UNRESTRICTED("0055003","제한없음");

    private final String key;
    private final String name;

    public static Marriage fromKey(String policyNum, String key) {
        if (key == null) return Marriage.UNRESTRICTED;

        return switch(key){
                case "0055001" -> Marriage.MARRIED;
                case "0055002" -> Marriage.SINGLE;
                case "0055003" -> Marriage.UNRESTRICTED;
                default -> Marriage.UNRESTRICTED; // 기본값
        };
    }
}
