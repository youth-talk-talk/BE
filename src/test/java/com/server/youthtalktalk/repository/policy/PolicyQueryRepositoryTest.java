package com.server.youthtalktalk.repository.policy;

import static com.server.youthtalktalk.domain.policy.entity.Category.JOB;
import static com.server.youthtalktalk.domain.policy.entity.Category.LIFE;
import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.*;
import static com.server.youthtalktalk.domain.policy.entity.condition.Earn.ANNUL_INCOME;
import static com.server.youthtalktalk.domain.policy.entity.condition.Earn.OTHER;
import static com.server.youthtalktalk.domain.policy.entity.condition.Earn.UNRESTRICTED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Education.UNIVERSITY_GRADUATED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Education.UNIVERSITY_GRADUATED_EXPECTED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Employment.EMPLOYED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Employment.FREELANCER;
import static com.server.youthtalktalk.domain.policy.entity.condition.Major.BUSINESS;
import static com.server.youthtalktalk.domain.policy.entity.condition.Major.SCIENCE;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.SINGLE;
import static com.server.youthtalktalk.domain.policy.entity.condition.Specialization.DISABLED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Specialization.SOLDIER;
import static org.assertj.core.api.Assertions.assertThat;

import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.repository.region.PolicySubRegionRepository;
import com.server.youthtalktalk.domain.policy.repository.region.SubRegionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PolicyQueryRepositoryTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PolicySubRegionRepository policySubRegionRepository;

    @Autowired
    private SubRegionRepository subRegionRepository;

    @BeforeEach
    void setUp() {
        List<Policy> dummyPolicies = createDummyPolicies();
        List<SubRegion> dummySubRegions = createDummySubRegions();
        List<PolicySubRegion> dummyPolicySubRegions = createDummyPolicySubRegions(dummyPolicies, dummySubRegions);
        subRegionRepository.saveAll(dummySubRegions);
        policyRepository.saveAll(dummyPolicies);
        policySubRegionRepository.saveAll(dummyPolicySubRegions);
    }

    /**
     * 단일 조건 검색
     */
    @Test
    @DisplayName("키워드 검색")
    void testKeywordSearch() {
        String keyword = "1";
        SearchConditionDto condition = SearchConditionDto.builder().keyword(keyword).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getTitle().contains(keyword) || policy.getIntroduction().contains(keyword)).count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy -> policy.getIntroduction().contains(keyword) || policy.getTitle().contains(keyword));
    }

    @Test
    @DisplayName("운영기관 타입으로 검색")
    void testSearchByInstitutionType() {
        SearchConditionDto condition = SearchConditionDto.builder().institutionType(CENTER).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getInstitutionType().equals(CENTER)).count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy -> policy.getInstitutionType() == CENTER);
    }

    @Test
    @DisplayName("카테고리로 검색")
    void testSearchByCategory() {
        List<Category> categories = List.of(JOB, LIFE);
        SearchConditionDto condition = SearchConditionDto.builder().categories(categories).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getCategory().equals(JOB) ||
                        policy.getCategory().equals(LIFE))
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(data -> categories.contains(data.getCategory()));
    }

    @Test
    @DisplayName("지역으로 검색")
    void testSearchByRegions() {
        List<SubRegion> allSubRegions = subRegionRepository.findAll();
        List<Long> subRegionIds = List.of(allSubRegions.get(1), allSubRegions.get(3), allSubRegions.get(5)).stream()
                .map(SubRegion::getId).toList();
        SearchConditionDto condition = SearchConditionDto.builder().subRegionIds(subRegionIds).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getPolicySubRegions().stream()
                        .anyMatch(psr -> subRegionIds.contains(psr.getSubRegion().getId())))
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy ->
                policy.getPolicySubRegions().stream()
                        .anyMatch(psr -> subRegionIds.contains(psr.getSubRegion().getId()))
        );
    }

    @Test
    @DisplayName("결혼요건으로 검색")
    void testSearchByMarriage() {
        SearchConditionDto condition = SearchConditionDto.builder().marriage(SINGLE).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getMarriage().equals(SINGLE)).count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy -> policy.getMarriage() == SINGLE);
    }

    @Test
    @DisplayName("나이으로 검색")
    void testSearchByAge() {
        int age = 20;
        SearchConditionDto condition = SearchConditionDto.builder().age(age).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> !policy.isLimitedAge() ||
                        (policy.getMinAge() <= age && policy.getMaxAge() >= age))
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy -> policy.getMinAge() <= age && policy.getMaxAge() >= age);
    }

    @Test
    @DisplayName("연소득으로 검색")
    void testSearchByEarn() {
        int minEarn = 1000;
        int maxEarn = 2000;
        SearchConditionDto condition = SearchConditionDto.builder().minEarn(minEarn).maxEarn(maxEarn).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy ->
                        (policy.getEarn() == UNRESTRICTED || policy.getEarn() == OTHER) ||  // 제한이 없으면 무조건 포함
                        (policy.getMinEarn() <= minEarn && policy.getMaxEarn() >= maxEarn) // 제한 있으면 조건 만족해야 포함
                )
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy ->
                !policy.isLimitedAge() || (policy.getMinAge() <= minEarn && policy.getMaxAge() >= maxEarn)
        );
    }

    @Test
    @DisplayName("학력으로 검색")
    void testSearchByEducation() {
        List<Education> educations = List.of(UNIVERSITY_GRADUATED, UNIVERSITY_GRADUATED_EXPECTED);
        SearchConditionDto condition = SearchConditionDto.builder().educations(educations).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getEducation() != null &&
                        policy.getEducation().stream().anyMatch(educations::contains)
                )
                .count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(
                policy -> policy.getEducation() != null &&
                        policy.getEducation().stream().anyMatch(educations::contains)
        );
    }

    @Test
    @DisplayName("전공요건으로 검색")
    void testSearchByMajor() {
        List<Major> majors = List.of(SCIENCE, BUSINESS);
        SearchConditionDto condition = SearchConditionDto.builder().majors(majors).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getMajor() != null &&
                        policy.getMajor().stream().anyMatch(majors::contains)
                )
                .count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(
                policy -> policy.getMajor() != null &&
                        policy.getMajor().stream().anyMatch(majors::contains)
        );
    }

    @Test
    @DisplayName("취업상태로 검색")
    void testSearchByEmployment() {
        List<Employment> employments = List.of(FREELANCER, EMPLOYED);
        SearchConditionDto condition = SearchConditionDto.builder().employments(employments).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getEmployment() != null &&
                        policy.getEmployment().stream().anyMatch(employments::contains)
                )
                .count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(
                policy -> policy.getEmployment() != null &&
                        policy.getEmployment().stream().anyMatch(employments::contains)
        );
    }

    @Test
    @DisplayName("특화분야로 검색")
    void testSearchBySpecialization() {
        List<Specialization> specializations = List.of(SOLDIER, DISABLED);
        SearchConditionDto condition = SearchConditionDto.builder().specializations(specializations).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getSpecialization() != null &&
                        policy.getSpecialization().stream().anyMatch(specializations::contains)
                )
                .count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(
                policy -> policy.getSpecialization() != null &&
                        policy.getSpecialization().stream().anyMatch(specializations::contains)
        );
    }

    static List<Policy> createDummyPolicies() {
        List<Policy> policies = new ArrayList<>();

        InstitutionType[] institutionTypes = InstitutionType.values();
        Category[] categories = Category.values();
        Region[] regions = Region.values();
        Marriage[] marriages = Marriage.values();
        Education[] educations = Education.values();
        Major[] majors = Major.values();
        Employment[] employments = Employment.values();
        Specialization[] specializations = Specialization.values();

        for (int i = 0; i < 20; i++) {
            Policy policy = Policy.builder()
                    .policyId(UUID.randomUUID().toString())
                    .isLimitedAge(true)
                    .earn(ANNUL_INCOME)
                    .institutionType(institutionTypes[i % institutionTypes.length])
                    .title("청년 정책 " + i)
                    .introduction("소개 내용 " + i)
                    .region(regions[i % regions.length])
                    .category(categories[i % categories.length])
                    .region(regions[i % regions.length])
                    .marriage(marriages[i % marriages.length])
                    .minAge(18 + (i % 6)) // 18~23
                    .maxAge(29 + (i % 6)) // 29~34
                    .minEarn(2000 + i * 100)
                    .maxEarn(3000 + i * 100)
                    .education(List.of(educations[i % educations.length]))
                    .specialization(List.of(
                            specializations[i % specializations.length],
                            specializations[(i + 1) % specializations.length]
                    ))
                    .major(List.of(majors[i % majors.length], majors[(i + 2) % majors.length]))
                    .employment(List.of(employments[i % employments.length]))
                    .build();

            policies.add(policy);
        }

        return policies;
    }

    private static List<SubRegion> createDummySubRegions() {
        List<SubRegion> subRegions = new ArrayList<>();
        Region[] regions = Region.values();
        for (int i = 0; i < 10; i++) {
            SubRegion subRegion = SubRegion.builder()
                    .region(regions[i % regions.length])
                    .name("subregion" + i)
                    .code("code" + i)
                    .build();
            subRegions.add(subRegion);
        }
        return subRegions;
    }

    private static List<PolicySubRegion> createDummyPolicySubRegions(List<Policy> dummyPolicies, List<SubRegion> subRegions) {
        List<PolicySubRegion> policySubRegions = new ArrayList<>();
        for (int i = 0; i < dummyPolicies.size(); i++) {
            SubRegion subRegion = subRegions.get(i % subRegions.size());
            PolicySubRegion policySubRegion = PolicySubRegion.builder().build();
            policySubRegion.setSubRegion(subRegion);
            policySubRegion.setPolicy(dummyPolicies.get(i));
            policySubRegions.add(policySubRegion);
        }
        return policySubRegions;
    }
}