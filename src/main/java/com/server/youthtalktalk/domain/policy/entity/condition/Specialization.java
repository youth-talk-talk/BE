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
public enum Specialization {
    SMALL_BUSINESS("0014001", "중소기업"),
    WOMEN("0014002", "여성"),
    WELFARE_RECIPIENT("0014003", "기초생활수급자"),
    SINGLE_PARENT("0014004", "한부모가정"),
    DISABLED("0014005", "장애인"),
    FARMER("0014006", "농업인"),
    SOLDIER("0014007", "군인"),
    REGIONAL_TALENT("0014008", "지역인재"),
    OTHER("0014009", "기타"),
    UNRESTRICTED("0014010", "제한없음");

    private final String key;
    private final String name;

    public static Specialization fromKey(String policyNum, String key) {
        for (Specialization specialization : Specialization.values()) {
            if (specialization.getKey().equals(key)) {
                return specialization;
            }
        }
        log.error("[Policy Data] Not Existed Specialization = {}, policyNum = {}", key, policyNum);
        throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_SPECIALIZATION);
    }

    public static List<Specialization> findSpecializationList(String policyNum, String data){
        String[] specializations = data.split(",");
        Set<String> set = new HashSet<>();
        Collections.addAll(set, specializations);

        List<Specialization> specializationList = new ArrayList<>();
        for(Specialization specialization : Specialization.values()) {
            if(set.contains(specialization.getKey())) {
                specializationList.add(specialization);
            }
        }

        if(set.size() != specializationList.size()) {
            log.error("[Policy Data] Not Existed Specialization = {}, policyNum = {}", data, policyNum);
            throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_SPECIALIZATION);
        }
        return specializationList;
    }
}
