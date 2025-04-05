package com.server.youthtalktalk.domain.policy.service;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.*;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.condition.*;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.repository.region.SubRegionRepository;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

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
    public PolicyDetailResponseDto getPolicyDetail(Long policyId){
        Long memberId;
        try {
            memberId = memberService.getCurrentMember().getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        Policy policy = policyRepository.findByPolicyId(policyId)
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
        String keyword = parseKeyword(conditionDto.getKeyword());
        InstitutionType institutionType = parseInstitutionType(conditionDto.getInstitutionType());
        List<Category> categories = parseCategories(conditionDto.getCategory());
        Marriage marriage = parseMarriage(conditionDto.getMarriage());
        Integer age = parseAge(conditionDto.getAge());
        Integer minEarn = parseEarn(conditionDto.getMinEarn());
        Integer maxEarn = parseEarn(conditionDto.getMaxEarn());
        List<Education> educations = getEnumListFromStrings(conditionDto.getEducation(), Education.class);
        List<Major> majors = getEnumListFromStrings(conditionDto.getMajor(), Major.class);
        List<Employment> employments = getEnumListFromStrings(conditionDto.getEmployment(), Employment.class);
        List<Specialization> specializations = getEnumListFromStrings(conditionDto.getSpecialization(), Specialization.class);
        List<Long> subRegionIds = parseSubRegionIds(conditionDto.getRegion());

        return SearchConditionDto.builder()
                .keyword(keyword)
                .institutionType(institutionType)
                .categories(categories)
                .marriage(marriage)
                .age(age)
                .minEarn(minEarn)
                .maxEarn(maxEarn)
                .educations(educations)
                .majors(majors)
                .employments(employments)
                .specializations(specializations)
                .subRegionIds(subRegionIds)
                .isFinished(conditionDto.getIsFinished())
                .build();
    }

    private <T extends Enum<T>> List<T> getEnumListFromStrings(List<String> values, Class<T> enumType) {
        List<T> enumList = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                try {
                    enumList.add(Enum.valueOf(enumType, trimmedValue(value).toUpperCase()));
                } catch (IllegalArgumentException e) {
                    switch (enumType.getSimpleName()) {
                        case "Education" -> throw new InvalidValueException(INVALID_EDUCATION);
                        case "Major" -> throw new InvalidValueException(INVALID_MAJOR);
                        case "Specialization" -> throw new InvalidValueException(INVALID_SPECIALIZATION);
                        case "Employment" -> throw new InvalidValueException(INVALID_EMPLOYMENT);
                    }
                }
            }
        }
        return enumList;
    }

    private String trimmedValue(String value) {
        return value.trim().replaceAll("\\s+", "");
    }

    private String parseKeyword(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isBlank()) {
            throw new InvalidValueException(INVALID_KEYWORD); // 공백 검색어 예외
        }
        return trimmed;
    }

    private InstitutionType parseInstitutionType(String input) {
        InstitutionType institutionType = null;
        try {
            if (input != null && !input.isBlank()) {
                institutionType = InstitutionType.valueOf(trimmedValue(input).toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_INSTITUTION_TYPE);
        }
        return institutionType;
    }

    private List<Category> parseCategories(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<Category> categories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            String trimmedName = trimmedValue(categoryName).toUpperCase();
            resolveToCategory(categories, trimmedName);
        }
        return categories;
    }

    private void resolveToCategory(List<Category> categories, String name) {
        try {
            Category category = Category.valueOf(name);
            categories.add(category);
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_CATEGORY);
        }
    }

    private Marriage parseMarriage(String input) {
        Marriage marriage = null;
        try {
            if (input != null && !input.isBlank()) {
                marriage = Marriage.valueOf(trimmedValue(input).toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_MARRIAGE);
        }
        return marriage;
    }

    private Integer parseAge(String input) {
        Integer age = null;
        try {
            if (input != null && !input.isBlank()) {
                age = Integer.parseInt(trimmedValue(input));
                if (age < MIN_AGE_INPUT || age > MAX_AGE_INPUT) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_AGE);
        }
        return age;
    }

    private Integer parseEarn(String input) {
        Integer earn = null;
        try {
            if (input != null && input.isBlank()) {
                earn = Integer.parseInt(trimmedValue(input));
                if (earn < MIN_EARN_INPUT || earn > MAX_EARN_INPUT) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(INVALID_EARN);
        }
        return earn;
    }

    private List<Long> parseSubRegionIds(List<String> regionNames) {
        if (regionNames == null || regionNames.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> subRegionIds = new LinkedHashSet<>();
        for (String regionName : regionNames) {
            String trimmedRegionName = trimmedValue(regionName);
            resolveToSubRegionId(subRegionIds, trimmedRegionName);
        }
        return new ArrayList<>(subRegionIds); // 중복 제거 및 순서 유지
    }

    private void resolveToSubRegionId(Set<Long> subRegionIds, String name) {
        Region region = Region.fromName(name); // 지역 입력값이 상위 지역 이름과 매핑 시도

        // 상위 지역과 매핑 성공 시, 상위 지역이 갖고 있는 모든 하위 지역 저장
        if (region != null) {
            subRegionIds.addAll(
                    subRegionRepository.findAllByRegion(region).stream().map(SubRegion::getId).toList()
            );
            return;
        }

        // 상위 지역과 매핑 실패 시, 하위 지역과 직접 매핑 시도
        subRegionRepository.findByName(name)
                .map(SubRegion::getId)
                .ifPresentOrElse(
                        subRegionIds::add,
                        () -> {throw new InvalidValueException(INVALID_REGION);} // 하위 지역에서도 못 찾으면 예외 발생
                );
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
                    Long policyId = policy.getPolicyId();
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
    public Scrap scrapPolicy(Long policyId, Member member) {
        policyRepository.findByPolicyId(policyId).orElseThrow(PolicyNotFoundException::new); // 정책 존재 유무
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

