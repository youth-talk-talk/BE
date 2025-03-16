package com.server.youthtalktalk.domain.policy.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    JOB("001", "일자리"),
    DWELLING("002", "주거"),
    EDUCATION("003", "교육"),
    LIFE("004", "생활지원"),
    PARTICIPATION("005", "참여");

    private final String key;
    private final String name;

    public static Category fromKey(String policyId, String key){
        return switch(key){
            case "001" -> Category.JOB;
            case "002" -> Category.DWELLING;
            case "003" -> Category.EDUCATION;
            case "004" -> Category.LIFE;
            case "005" -> Category.PARTICIPATION;
            default -> throw new RuntimeException("Illegal Category :" + policyId);
        };
    }

    public static Category fromName(String name) {
        for (Category category : Category.values()) {
            if (category.name.equals(name)) {
                return category;
            }
        }
        return null;
    }
}
