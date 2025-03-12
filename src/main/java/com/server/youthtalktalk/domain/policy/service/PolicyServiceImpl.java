package com.server.youthtalktalk.domain.policy.service;

import static com.server.youthtalktalk.global.response.BaseResponseCode.INVALID_INPUT_VALUE;
import static com.server.youthtalktalk.global.response.BaseResponseCode.INVALID_REGION_NAME;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.SubCategory;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.region.SubRegionRepository;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.dto.*;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.domain.member.service.MemberService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    public static final int MIN_AGE_INPUT = 8;
    public static final int MAX_AGE_INPUT = 100;
    public static final int MIN_EARN_INPUT = 0;
    public static final int MAX_EARN_INPUT = 2_000_000_000;

    private final PolicyRepository policyRepository;
    private final ScrapRepository scrapRepository;
    private final MemberService memberService;
    private final SubRegionRepository subRegionRepository;

    /**
     * top 5 정책 조회
     */
    @Override
    public List<PolicyListResponseDto> getTop5Policies() {
        Long memberId;
        Region region;
        try {
            memberId = memberService.getCurrentMember().getId();
            region = memberService.getCurrentMember().getRegion();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        PageRequest pageRequest = PageRequest.of(0, 5); // top 5
        List<Policy> policies = policyRepository.findTop5ByRegionOrderByViewsDesc(region, pageRequest).getContent();
        if (policies.isEmpty()) {
            throw new PolicyNotFoundException();
        }

        List<PolicyListResponseDto> result = policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
        log.info("상위 5개 정책 조회 성공");
        return result;
    }


    /**
     * 카테고리 별 정책 조회
     */
    @Override
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable) {
        Long memberId;
        Region region;
        try {
            memberId = memberService.getCurrentMember().getId();
            region = memberService.getCurrentMember().getRegion();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<Policy> policies = policyRepository.findByRegionAndCategory(region, categories, pageable).getContent();
        if (policies.isEmpty()) {
            throw new PolicyNotFoundException();
        }
        List<PolicyListResponseDto> result =  policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
        log.info("카테고리별 정책 조회 성공");
        return result;
    }


    /**
     * 특정 정책 세부 조회
     */
    @Override
    public PolicyDetailResponseDto getPolicyDetail(String policyId){
        Long memberId;
        try {
            memberId = memberService.getCurrentMember().getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(PolicyNotFoundException::new);

        policyRepository.save(policy.toBuilder().view(policy.getView()+1).build());

        boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
        PolicyDetailResponseDto result = PolicyDetailResponseDto.toDto(policy, isScrap);
        log.info("특정 정책 세부 조회 성공");
        return result;
    }

    /**
     * 조건 적용 정책 조회
     */
    public SearchConditionResponseDto getPoliciesByCondition(SearchConditionRequestDto conditionDto, Pageable pageable) {

        SearchConditionDto searchCondition = setSearchCondition(conditionDto);

        Page<Policy> policies = policyRepository.findByCondition(searchCondition, pageable);

        if (policies.isEmpty()) {
            log.info("조건에 맞는 정책이 존재하지 않습니다");
            return SearchConditionResponseDto.toListDto(Collections.emptyList(), 0L); // 빈 리스트 반환
        }

        List<PolicyListResponseDto> result = policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(
                            memberService.getCurrentMember().getId(), policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
        log.info("조건 적용 정책 조회 성공");

        return SearchConditionResponseDto.toListDto(result, policies.getTotalElements());
    }

    private SearchConditionDto setSearchCondition(SearchConditionRequestDto conditionDto) {
        String keyword = getKeyword(conditionDto.getKeyword());
        InstitutionType institutionType = InstitutionType.fromString(conditionDto.getInstitutionType());
        List<SubCategory> subCategories = getSubCategories(conditionDto);
        List<SubRegion> subRegions = getSubRegions(conditionDto);
        Marriage marriage = getMarriage(conditionDto);
        Integer age = getAge(conditionDto);
        List<String> earnList = conditionDto.getEarn();
        Integer minEarn = (earnList != null && !earnList.isEmpty()) ? getEarn(earnList.getFirst()) : null;
        Integer maxEarn = (earnList != null && !earnList.isEmpty()) ? getEarn(earnList.getLast()) : null;
        List<Education> educations = getEnumListFromStrings(conditionDto.getEducation(), Education.class);
        List<Major> majors = getEnumListFromStrings(conditionDto.getMajor(), Major.class);
        List<Employment> employments = getEnumListFromStrings(conditionDto.getEmployment(), Employment.class);
        List<Specialization> specializations = getEnumListFromStrings(conditionDto.getSpecialization(), Specialization.class);

        return SearchConditionDto.builder()
                .keyword(keyword)
                .institutionType(institutionType)
                .subCategories(subCategories)
                .subRegions(subRegions)
                .marriage(marriage)
                .age(age)
                .minEarn(minEarn)
                .maxEarn(maxEarn)
                .educations(educations)
                .majors(majors)
                .employments(employments)
                .specializations(specializations)
                .build();
    }

    private <T extends Enum<T>> List<T> getEnumListFromStrings(List<String> values, Class<T> enumType) {
        List<T> enumList = new ArrayList<>();
        try {
            if (values != null && !values.isEmpty()) {
                for (String value : values) {
                    enumList.add(Enum.valueOf(enumType, trimmedValue(value))); // enumType에 맞춰서 valueOf 호출
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        return enumList;
    }

    private String trimmedValue(String value) {
        return value.trim().replaceAll("\\s+", "");
    }

    private String getKeyword(String input) {
        return (input == null || input.isBlank()) ? null : input.trim();
    }

    private List<SubCategory> getSubCategories(SearchConditionRequestDto conditionDto) {
        List<String> categoryNames = conditionDto.getCategory();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<SubCategory> subCategories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            if (Category.fromName(categoryName) != null) { // 상위 카테고리를 전체 선택하는 경우
                Category category = Category.fromName(categoryName);
                subCategories.addAll(SubCategory.fromCategory(category));
                continue;
            }
            subCategories.add(SubCategory.fromName(categoryName));
        }
        return subCategories;
    }

    private List<SubRegion> getSubRegions(SearchConditionRequestDto conditionDto) {
        List<String> regionNames = conditionDto.getRegion();
        if (regionNames == null || regionNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<SubRegion> subRegions = new ArrayList<>();
        for (String regionName : regionNames) {
            if (Region.fromName(regionName) != null) { // 상위 지역을 전체 선택하는 경우
                Region region = Region.fromName(regionName);
                List<SubRegion> allSubRegionsByRegion = subRegionRepository.findAllByRegion(region);
                if (allSubRegionsByRegion.isEmpty()) {
                    throw new InvalidValueException(INVALID_REGION_NAME);
                }
                subRegions.addAll(allSubRegionsByRegion);
                continue;
            }
            subRegionRepository.findByName(regionName)
                    .map(subRegions::add)
                    .orElseThrow(() -> new InvalidValueException(INVALID_REGION_NAME));
        }
        return subRegions;
    }

    private Marriage getMarriage(SearchConditionRequestDto conditionDto) {
        Marriage marriage = null;
        try {
            if (conditionDto.getMarriage() != null) {
                marriage = Marriage.valueOf(trimmedValue(conditionDto.getMarriage()));
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        return marriage;
    }

    private Integer getAge(SearchConditionRequestDto conditionDto) {
        Integer age = null;
        try {
            if (conditionDto.getAge() != null) {
                age = Integer.parseInt(trimmedValue(conditionDto.getAge()));
                if (age < MIN_AGE_INPUT || age > MAX_AGE_INPUT) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        return age;
    }

    private Integer getEarn(String input) {
        Integer earn = null;
        try {
            if (input != null && input.isBlank()) {
                earn = Integer.parseInt(trimmedValue(input));
                if (earn < MIN_EARN_INPUT || earn > MAX_EARN_INPUT) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_INPUT_VALUE);
        }
        return earn;
    }

    /**
     * 이름으로 정책 조회
     */
    public List<SearchNameResponseDto> getPoliciesByName(String title, Pageable pageable){
        title = title.replace(" ", "");
        Region region;
        try {
            region = memberService.getCurrentMember().getRegion();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<Policy> policies = policyRepository.findByRegionAndTitle(region, title, pageable).getContent();

        if (policies.isEmpty()) {
            log.info("해당 이름의 정책이 존재하지 않습니다");
            return Collections.emptyList(); // 빈 리스트 반환
        }

        List<SearchNameResponseDto> result = policies.stream()
                .map(policy -> {
                    String policyTitle = policy.getTitle();
                    String policyId = policy.getPolicyId();
                    return SearchNameResponseDto.builder()
                            .policyId(policyId)
                            .title(policyTitle)
                            .build();
                })
                .collect(Collectors.toList());
        log.info("이름으로 정책 조회 성공");
        return result;
    }



    @Override
    public Scrap scrapPolicy(String policyId, Member member) {
        policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new); // 정책 존재 유무
        Scrap scrap = scrapRepository.findByMemberAndItemIdAndItemType(member,policyId, ItemType.POLICY).orElse(null);
        if(scrap == null){
            return scrapRepository.save(Scrap.builder() // 스크랩할 경우
                    .itemId(policyId)
                    .itemType(ItemType.POLICY)
                    .member(member)
                    .build());
        }
        else{
            scrapRepository.delete(scrap);
            return null;
        }
    }

    @Override
    public List<PolicyListResponseDto> getScrapPolicies(Pageable pageable, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Policy> policies = policyRepository.findAllByScrap(member, pageRequest).getContent();
        return policies.stream()
                .map(policy -> {
                    boolean isScrap = true;
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
    }

    /**
     * 스크랩한 마감 임박 정책 조회 (최대 5개)
     */
    @Override
    public List<PolicyListResponseDto> getScrappedPoliciesWithUpcomingDeadline(Member member){
        PageRequest pageRequest = PageRequest.of(0, 5); // top 5
        List<Policy> policies = policyRepository.findTop5OrderByDeadlineAsc(member, pageRequest).getContent();
        return policies.stream()
                .map(policy -> {
                    boolean isScrap = true;
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
    }




}

