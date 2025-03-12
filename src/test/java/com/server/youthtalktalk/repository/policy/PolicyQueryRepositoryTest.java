package com.server.youthtalktalk.repository.policy;

import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.*;
import static com.server.youthtalktalk.domain.policy.entity.SubCategory.*;
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
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.SubCategory;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<SubCategory> subCategories = List.of(JOB_CULTURE, JOB_SAFETY, JOB_EXPANSION, JOB_STARTUP, DWELLING_SUPPLY);
        Set<SubCategory> subCategorySet = new HashSet<>(subCategories);
        SearchConditionDto condition = SearchConditionDto.builder().subCategories(subCategories).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getSubCategory().equals(JOB_CULTURE) ||
                        policy.getSubCategory().equals(JOB_SAFETY) ||
                        policy.getSubCategory().equals(JOB_EXPANSION) ||
                        policy.getSubCategory().equals(JOB_STARTUP) ||
                        policy.getSubCategory().equals(DWELLING_SUPPLY))
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(data -> subCategorySet.contains(data.getSubCategory()));
    }

    @Test
    @DisplayName("지역으로 검색")
    void testSearchByRegions() {
        List<SubRegion> allSubRegions = subRegionRepository.findAll();
        List<SubRegion> targetSubRegions = List.of(allSubRegions.get(1), allSubRegions.get(3), allSubRegions.get(5));
        SearchConditionDto condition = SearchConditionDto.builder().subRegions(targetSubRegions).build();
        List<Policy> result = policyRepository.findByCondition(condition, PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        long expectedCount = policyRepository.findAll().stream()
                .filter(policy -> policy.getPolicySubRegions().stream()
                        .anyMatch(psr -> targetSubRegions.contains(psr.getSubRegion())))
                .count();

        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy ->
                policy.getPolicySubRegions().stream()
                        .anyMatch(psr -> targetSubRegions.contains(psr.getSubRegion()))
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
                .filter(policy -> policy.getMinAge() <= age && policy.getMaxAge() >= age)
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
                .filter(policy -> policy.getMinEarn() <= minEarn && policy.getMaxEarn() >= maxEarn).count();
        assertThat(result.size()).isEqualTo(expectedCount);
        assertThat(result).allMatch(policy -> policy.getMinEarn() <= minEarn && policy.getMaxEarn() >= maxEarn);
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
        SubCategory[] subCategories = SubCategory.values();
        Region[] regions = Region.values();
        Marriage[] marriages = Marriage.values();
        Education[] educations = Education.values();
        Major[] majors = Major.values();
        Employment[] employments = Employment.values();
        Specialization[] specializations = Specialization.values();

        for (int i = 0; i < 20; i++) {
            Policy policy = Policy.builder()
                    .policyId(UUID.randomUUID().toString())
                    .institutionType(institutionTypes[i % institutionTypes.length])
                    .title("청년 정책 " + i)
                    .introduction("소개 내용 " + i)
                    .region(regions[i % regions.length])
                    .subCategory(subCategories[i % subCategories.length])
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