package com.server.youthtalktalk.domain.policy.entity.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public enum Employment {
    EMPLOYED("0013001", "재직자"),
    SELF_EMPLOYED("0013002", "자영업자"),
    UNEMPLOYED("0013003", "미취업자"),
    FREELANCER("0013004", "프리랜서"),
    DAILY_WORKER("0013005", "일용근로자"),
    ENTREPRENEUR("0013006", "(예비)창업자"),
    TEMPORARY_WORKER("0013007", "단기근로자"),
    FARMER("0013008", "영농종사자"),
    OTHER("0013009", "기타"),
    UNRESTRICTED("0013010", "제한없음");

    private final String key;
    private final String name;

    public static Employment fromKey(String key) {
        for (Employment employment : Employment.values()) {
            if(employment.getKey().equals(key)) {
                return employment;
            }
        }
        throw new RuntimeException("Not Existed Employment");
    }

    public static List<Employment> findEmploymentList(String policyId, String data){
        String[] employments = data.split(",");
        Set<String> set = new HashSet<>();
        Collections.addAll(set, employments);

        List<Employment> employmentList = new ArrayList<>();
        for(Employment employment : Employment.values()) {
            if(set.contains(employment.getKey())) {
                employmentList.add(employment);
            }
        }

        if(set.size() != employmentList.size()) {
            throw new RuntimeException("Not Existed Employment : " + policyId);
        }
        return employmentList;
    }

}
