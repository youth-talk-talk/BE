package com.server.youthtalktalk.repository.policy;

import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.*;
import static com.server.youthtalktalk.domain.policy.entity.RepeatCode.PERIOD;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.SINGLE;
import static com.server.youthtalktalk.domain.policy.entity.region.Region.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PolicyRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Test
    @DisplayName("조회수가 가장 높은 5개의 정책을 정렬하여 반환한다.")
    void testFindTop5ByOrderByViewDesc() {
        // given
        for (int i = 1; i <= 10; i++) {
            Policy policy = Policy.builder()
                    .view(i * 10) // 10, 20, ..., 100
                    .policyNum("policy" + i)
                    .title("title" + i)
                    .region(SEOUL)
                    .institutionType(LOCAL)
                    .repeatCode(PERIOD)
                    .marriage(SINGLE)
                    .build();

            policyRepository.save(policy);
        }

        // when
        List<Policy> top5 = policyRepository.findTop5ByOrderByViewDescPolicyNumDesc();

        // then
        assertThat(top5.size()).isEqualTo(5); // 크기 검증 (최대 5개)
        assertThat(top5)
                .extracting(Policy::getView)
                .containsExactly(100L, 90L, 80L, 70L, 60L); // 순서 및 값 검증
    }

    @Test
    @DisplayName("조회수가 같을 경우 policyNum 내림차순으로 정렬된다. (최신순 정렬)")
    void testSameViewOrdersByPolicyNumDesc() {
        // given
        Policy policy1 = Policy.builder()
                .view(100L)
                .policyNum("policyA")
                .title("A")
                .region(SEOUL)
                .institutionType(LOCAL)
                .repeatCode(PERIOD)
                .marriage(SINGLE)
                .build();

        Policy policy2 = Policy.builder()
                .view(100L)
                .policyNum("policyB")
                .title("B")
                .region(SEOUL)
                .institutionType(LOCAL)
                .repeatCode(PERIOD)
                .marriage(SINGLE)
                .build();

        Policy policy3 = Policy.builder()
                .view(100L)
                .policyNum("policyC")
                .title("C")
                .region(SEOUL)
                .institutionType(LOCAL)
                .repeatCode(PERIOD)
                .marriage(SINGLE)
                .build();

        policyRepository.saveAll(List.of(policy1, policy2, policy3));

        // when
        List<Policy> result = policyRepository.findTop5ByOrderByViewDescPolicyNumDesc();

        // then
        assertThat(result)
                .extracting(Policy::getPolicyNum)
                .containsExactly("policyC", "policyB", "policyA"); // policyNum 내림차순 (최신순)
    }
}
