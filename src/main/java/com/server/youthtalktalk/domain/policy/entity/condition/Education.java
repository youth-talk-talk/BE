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
public enum Education {
    HIGHSCHOOL_BELOW("0049001", "고졸 미만"),
    HIGHSCHOOL_STUDENT("0049002", "고교 재학"),
    HIGHSCHOOL_GRADUATED_EXPECTED("0049003", "고졸 예정"),
    HIGHSCHOOL_GRADUATED("0049004", "고교 졸업"),
    UNIVERSITY_STUDENT("0049005", "대학 재학"),
    UNIVERSITY_GRADUATED_EXPECTED("0049006", "대졸 예정"),
    UNIVERSITY_GRADUATED("0049007", "대학 졸업"),
    MASTER_DOCTOR("0049008", "석·박사"),
    OTHER("0049009", "기타"),
    UNRESTRICTED("0049010", "제한없음");

    private final String key;
    private final String name;

    public static Education fromKey(String key) {
        for(Education education : Education.values()) {
            if (education.getKey().equals(key)) {
                return education;
            }
        }
        log.error("[Policy Data] Not Existed Education = {}", key);
        throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_EDUCATION);
    }

    public static List<Education> findEducationList(String policyId, String data){
        String[] educations = data.split(",");
        Set<String> set = new HashSet<>();
        Collections.addAll(set, educations);

        List<Education> educationList = new ArrayList<>();
        for(Education education : Education.values()) {
            if(set.contains(education.getKey())) {
                educationList.add(education);
            }
        }

        if(set.size() != educationList.size()) {
            log.error("[Policy Data] Not Existed Education = {} policyId = {}", data, policyId);
            throw new FailPolicyDataException(BaseResponseCode.FAIL_POLICY_DATA_EDUCATION);
        }
        return educationList;
    }
}
