package com.server.youthtalktalk.domain.policy.entity;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.policy.FailPolicyDataException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.server.youthtalktalk.domain.policy.entity.Category.*;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum SubCategory {
    JOB_EXPANSION("001","일자리 확대 및 역량 강화", JOB),
    JOB_STARTUP("002","창업", JOB),
    JOB_CULTURE("003","직장 문화 개선", JOB),
    JOB_SAFETY("004","일터 안정망 강화", JOB),
    DWELLING_EXPANSION("005","주택 공급 확대", DWELLING),
    DWELLING_COST("006","비용 경감", DWELLING),
    DWELLING_SUPPORT("007","주거 취약청년 지원", DWELLING),
    DWELLING_SUPPLY("008","청년 친화형 주거 보급", DWELLING),
    EDUCATION_CAPACITY("009","미래역량 강화", EDUCATION),
    EDUCATION_EQUALITY("010","평등 교육 기회", EDUCATION),
    EDUCATION_JOB("011","일자리 연계", EDUCATION),
    EDUCATION_ONTACT("012","온택트 교육", EDUCATION),
    LIFE_FINANCE("013","금융 지원", LIFE),
    LIFE_VULNERABLE("014","취약계층 지원", LIFE),
    LIFE_HEALTH("015","건강", LIFE),
    LIFE_CULTURE("016","문화", LIFE),
    PARTICIPATION_POLICY("017","정책 결정 청년 참여 확대", PARTICIPATION),
    PARTICIPATION_FOUNDATION("018","정책 추진 기반", PARTICIPATION),
    PARTICIPATION_INFRA("019","정책 전달 체계 확립", PARTICIPATION),
    PARTICIPATION_PROTECT("020","권익 보호 및 교류 활성화", PARTICIPATION),;

    private final String key;
    private final String name;
    private final Category category;

    public static SubCategory fromKey(String policyNum, String key) {
        for (SubCategory subCategory : SubCategory.values()) {
            if (subCategory.getKey().equals(key)) {
                return subCategory;
            }
        }
        log.error("[Policy Data] Not Existed SubCategory = {} policyNum = {}", key, policyNum);
        throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_SUB_CATEGORY);
    }

    public static List<SubCategory> fromCategory(Category category) {
        List<SubCategory> subCategories = new ArrayList<>();
        for (SubCategory subCategory : SubCategory.values()) {
            if (subCategory.getCategory().equals(category)) {
                subCategories.add(subCategory);
            }
        }
        return subCategories;
    }
}
