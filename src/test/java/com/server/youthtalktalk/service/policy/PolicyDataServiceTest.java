package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.policy.service.data.PolicyDataService;
import org.assertj.core.api.Assertions;
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
<<<<<<< HEAD
    @DisplayName("정책 저장 성공")
    void successSaveData(){
//        policyDataService.saveData();
=======
    @DisplayName("신청 기간 파싱 성공")
    void successParsingApplyDate(){
        String applyDate = "20250401 ~ 20250430\\N20250801 ~ 20250829";
        String[] dates = applyDate.split("\\\\N");
        Assertions.assertThat(dates[0]).isEqualTo("20250401 ~ 20250430");
>>>>>>> dev/eunhye
    }
}
