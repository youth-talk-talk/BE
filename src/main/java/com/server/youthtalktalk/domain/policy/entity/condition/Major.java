package com.server.youthtalktalk.domain.policy.entity.condition;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.policy.FailPolicyDataException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum Major {
    HUMANITIES("0011001", "인문계열"),
    SOCIETY("0011002", "사회계열"),
    BUSINESS("0011003", "상경계열"),
    SCIENCE("0011004", "이학계열"),
    ENGINEERING("0011005", "공학계열"),
    ARTS_PHYSICAL("0011006", "예체능계열"),
    AGRICULTURE("0011007", "농산업계열"),
    OTHER("0011008", "기타"),
    UNRESTRICTED("0011009", "제한없음");

    private final String key;
    private final String name;

    public static Major fromKey(String key) {
        for (Major major : Major.values()) {
            if(major.getKey().equals(key)) {
                return major;
            }
        }
        log.error("[Policy Data] Not Existed Major = {}", key);
        throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_MAJOR);
    }

    public static List<Major> findMajorList(String policyId, String data){
        String[] majors = data.split(",");
        Set<String> set = new HashSet<>();
        Collections.addAll(set, majors);

        List<Major> majorList = new ArrayList<>();
        for(Major major : Major.values()) {
            if(set.contains(major.getKey())) {
                majorList.add(major);
            }
        }

        if(set.size() != majorList.size()) {
            log.error("[Policy Data] Not Existed Major = {} policyId = {}", data, policyId);
            throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_MAJOR);
        }
        return majorList;
    }
}
