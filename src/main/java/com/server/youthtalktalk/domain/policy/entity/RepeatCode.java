package com.server.youthtalktalk.domain.policy.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepeatCode {
    ALWAYS("0057002","상시"),
    PERIOD("0057001","특정기간"),
    UNDEFINED("0057003","마감");

    private final String key;
    private final String name;

    public static RepeatCode fromKey(String policyId, String key){
        return switch(key){
            case "0057001" -> RepeatCode.PERIOD;
            case "0057002" -> RepeatCode.ALWAYS;
            case "0057003" -> RepeatCode.UNDEFINED;
            default -> throw new RuntimeException("Illegal RepeatCode : " + policyId);
        };
    }
}
