package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.policy.service.data.PolicyDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PolicyDataServiceTest {
    @Autowired
    private PolicyDataService policyDataService;

    @Test
    @DisplayName("정책 저장 성공")
    void successSaveData(){
        policyDataService.saveData();
    }
}
