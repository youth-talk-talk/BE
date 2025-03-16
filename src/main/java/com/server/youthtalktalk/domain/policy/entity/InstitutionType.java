package com.server.youthtalktalk.domain.policy.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum InstitutionType {
    CENTER("0054001"),
    LOCAL("0054002");

    private final String key;
    // 담당 기관 타입 매핑
    public static InstitutionType fromKey(String policyId, String key){
        return switch (key) {
            case "0054001" -> InstitutionType.CENTER;
            case "0054002" -> InstitutionType.LOCAL;
            default -> {
                log.error("[Policy Data] Not Existed InstitutionType = {}, policyId = {}", key, policyId);
                yield InstitutionType.CENTER;
            }
        };
    }
}
