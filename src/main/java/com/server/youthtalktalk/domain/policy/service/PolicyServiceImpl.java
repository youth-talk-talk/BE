package com.server.youthtalktalk.domain.policy.service;

import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.*;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.repository.RecentViewedPolicyRepository;
import com.server.youthtalktalk.domain.policy.repository.region.SubRegionRepository;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustomImpl;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.*;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.server.youthtalktalk.domain.ItemType.*;
import static com.server.youthtalktalk.domain.comment.service.CommentServiceImpl.TIME_FORMAT;
import static com.server.youthtalktalk.domain.policy.entity.Category.*;
import static com.server.youthtalktalk.domain.policy.entity.region.Region.*;
import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    public static final int MIN_AGE_INPUT = 8;
    public static final int MAX_AGE_INPUT = 100;
    public static final int MIN_EARN_INPUT = 0;
    public static final String APPLY_DUE_FORMAT = "yyyy-MM-dd";
    public static final int POST_PREVIEW_MAX_LENGTH = 50;

    private final PolicyRepository policyRepository;
    private final ScrapRepository scrapRepository;
    private final MemberService memberService;
    private final SubRegionRepository subRegionRepository;
    private final PostRepositoryCustomImpl postRepository;
    private final RecentViewedPolicyRepository recentViewedPolicyRepository;

    /**
     * 관심지역의 인기 정책 조회 (top20)
     */
    @Override
    public List<PolicyListResponseDto> getPopularPoliciesInArea(Member member) {
        Region memberRegion = member.getRegion();
        Long memberId = member.getId();

        // 정렬 기준 및 데이터 개수 설정
        PageRequest pageRequest = PageRequest.of(0, 20, // 정책 20개
                Sort.by(
                        Sort.Order.desc("view"),
                        Sort.Order.desc("policyNum")
                )
        );

        // 관심지역 기반 필터링
        List<Policy> policies;
        if (memberRegion == NATIONWIDE) { // 관심지역이 전국이면 지역 필터링 X
            policies = policyRepository.findAll(pageRequest).getContent();
        } else { // 관심지역이 특정 지역이면 지역 필터링 O
            policies = policyRepository.findTop20ByRegion(memberRegion, pageRequest).getContent();
        }

        // DTO 변환
        List<PolicyListResponseDto> result = parsePolicyListResponseDto(policies, memberId);
        log.info("관심지역의 인기 정책 20개 조회 성공");

        return result;
    }

    public List<PolicyListResponseDto> parsePolicyListResponseDto(List<Policy> policies, Long memberId) {
        return policies.stream()
                .map(policy -> {
                    long scrapCount = scrapRepository.countByItemTypeAndItemId(ItemType.POLICY, policy.getPolicyId());
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap, scrapCount);
                })
                .toList();
    }

    /**
     * 카테고리 별 새로운 정책 조회
     */
    @Override
    public NewPoliciesResponseDto getNewPoliciesByCategory(Member member, String sortOption) {
        // 시간 범위 설정
        LocalDateTime from = LocalDate.now().minusDays(6).atStartOfDay(); // 6일 전 0시
        LocalDateTime to = LocalDate.now().plusDays(1).atStartOfDay(); // 내일 0시 (오늘 24시)

        // 정렬 기준 및 데이터 개수 설정
        Sort sort = switch (sortOption) {
            case "POPULAR" -> Sort.by(
                    Sort.Order.desc("view"), // 조회수 높은 순
                    Sort.Order.desc("policyNum") // 조회수 같으면 최신순
            );
            default -> Sort.by(Sort.Order.desc("policyNum")); // 최신순
        };

        // 카테고리별 새로운 정책 조회
        List<Policy> all = policyRepository.findByCreatedAtBetween(from, to, sort);
        List<Policy> job = policyRepository.findByCreatedAtBetweenAndCategory(from, to, JOB, sort);
        List<Policy> dwelling = policyRepository.findByCreatedAtBetweenAndCategory(from, to, DWELLING, sort);
        List<Policy> education = policyRepository.findByCreatedAtBetweenAndCategory(from, to, EDUCATION, sort);
        List<Policy> life = policyRepository.findByCreatedAtBetweenAndCategory(from, to, LIFE, sort);
        List<Policy> participation = policyRepository.findByCreatedAtBetweenAndCategory(from, to, PARTICIPATION, sort);

        // DTO 변환
        Long memberId = member.getId();
        NewPoliciesResponseDto result = new NewPoliciesResponseDto(
                parsePolicyListResponseDto(all, memberId),
                parsePolicyListResponseDto(job, memberId),
                parsePolicyListResponseDto(dwelling, memberId),
                parsePolicyListResponseDto(education, memberId),
                parsePolicyListResponseDto(life, memberId),
                parsePolicyListResponseDto(participation, memberId)
        );
        log.info("카테고리별 새로운 정책 조회 성공");

        return result;
    }

    /**
     * 후기가 많은 정책 top5 조회 (각 정책은 최대 3개의 후기와 함께 반환)
     */
    @Override
    public List<PolicyWithReviewsDto> getTop5PoliciesWithReviews(Member member) {
        List<Policy> topPolicies = policyRepository.findTop5OrderByReviewCount();

        return topPolicies.stream()
                .map(policy -> toPolicyWithReviewsDto(member, policy))
                .toList();
    }

    private PolicyWithReviewsDto toPolicyWithReviewsDto(Member member, Policy policy) {
        // 정책의 후기글 중에서 조회수 top3 조회
        List<Review> topReviews = postRepository.findTopReviewsByPolicy(member, policy, 3);

        List<ReviewInPolicyDto> reviews = topReviews.stream()
                .map(review -> toReviewInPolicyDto(review, member))
                .toList();

        return new PolicyWithReviewsDto(
                policy.getPolicyId(), // 정책 id
                policy.getTitle(), // 정책 제목
                policy.getDepartment().getImage_url(), // 정책 이미지 URL
                reviews // 정책의 인기 후기글 목록 (조회수 top3)
        );
    }

    private ReviewInPolicyDto toReviewInPolicyDto(Post review, Member member) {
        long scrapCount = scrapRepository.countByItemTypeAndItemId(POST, review.getId()); // 후기글의 스크랩 수 조회
        boolean scrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), review.getId(), POST); // 스크랩 여부

        return new ReviewInPolicyDto(
                review.getId(), // 게시글 id
                review.getTitle(), // 게시글 제목
                createContentSnippet(review.getContents().get(0).getContent()), // 내용 미리보기
                review.getPostComments().size(), // 댓글 수
                scrapCount, // 스크랩 수
                scrap, // 스크랩 여부
                review.getCreatedAt().format(DateTimeFormatter.ofPattern(TIME_FORMAT)) // 작성일
        );
    }

    private String createContentSnippet(String content) {
        return content.length() > POST_PREVIEW_MAX_LENGTH
                ? content.substring(0, POST_PREVIEW_MAX_LENGTH) + "..."
                : content;
    }

    /**
     * 특정 정책 세부 조회
     */
    @Override
    public PolicyDetailResponseDto getPolicyDetail(Long policyId){
        Member member;
        Long memberId;
        try {
            member = memberService.getCurrentMember();
            memberId = member.getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        Policy policy = policyRepository.findByPolicyId(policyId)
                .orElseThrow(PolicyNotFoundException::new);

        policyRepository.save(policy.toBuilder().view(policy.getView()+1).build());
        addRecentViewedPolicy(member, policy);

        boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), POLICY);
        PolicyDetailResponseDto result = PolicyDetailResponseDto.toDto(policy, isScrap);
        log.info("특정 정책 세부 조회 성공");
        return result;
    }

    @Override
    public PolicyPageResponseDto getAllPolicies(Pageable pageable, SortOption sortOption) {
        Page<Policy> policies = policyRepository.findAll(pageable, sortOption);

        if (policies.isEmpty()) {
            log.info("정책이 존재하지 않습니다.");
            return PolicyPageResponseDto.toListDto(Collections.emptyList(), 0L); // 빈 리스트 반환
        }

        List<PolicyListResponseDto> result = parsePolicyListResponseDto(policies.getContent(),
                memberService.getCurrentMember().getId());
        log.info("전체 정책 조회 성공");

        return PolicyPageResponseDto.toListDto(result, policies.getTotalElements());
    }

    /**
     * 조건 적용 정책 조회
     */
    public PolicyPageResponseDto getPoliciesByCondition(SearchConditionRequestDto conditionDto, Pageable pageable, SortOption sortOption) {
        SearchConditionDto searchCondition = setSearchCondition(conditionDto);
        Page<Policy> policies = policyRepository.findByCondition(searchCondition, pageable, sortOption);

        if (policies.isEmpty()) {
            log.info("조건에 맞는 정책이 존재하지 않습니다");
            return PolicyPageResponseDto.toListDto(Collections.emptyList(), 0L); // 빈 리스트 반환
        }

        List<PolicyListResponseDto> result = parsePolicyListResponseDto(policies.getContent(), memberService.getCurrentMember().getId());
        log.info("조건 적용 정책 조회 성공");

        return PolicyPageResponseDto.toListDto(result, policies.getTotalElements());
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
        LocalDate applyDue = parseApplyDue(conditionDto.getApplyDue());

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
                .applyDue(applyDue)
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
                if (earn < MIN_EARN_INPUT) {
                    throw new IllegalArgumentException();
                }
                if (earn >= 5000) earn = null; // 5000 이상이면 null로 설정해야 최대소득 제한이 없는 정책 검색 가능
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
        Region region = fromName(name); // 지역 입력값으로 상위 지역 찾기

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

    private LocalDate parseApplyDue(String dateStr) {
        LocalDate applyDue = null;
        if (dateStr == null || dateStr.isBlank()) return applyDue;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(APPLY_DUE_FORMAT);
            applyDue = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            // yyyy-MM-dd 형식에 맞지 않거나 존재하지 않는 날짜이면 예외 발생
            log.info("마감일 파싱 실패: {}", dateStr);
            throw new InvalidValueException(INVALID_APPLY_DUE);
        }
        return applyDue;
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
        Scrap scrap = scrapRepository.findByMemberAndItemIdAndItemType(member,policyId, POLICY).orElse(null);
        if(scrap == null){
            return scrapRepository.save(Scrap.builder() // 스크랩할 경우
                    .itemId(policyId)
                    .itemType(POLICY)
                    .member(member)
                    .createdAt(LocalDateTime.now())
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
                    long scrapCount = scrapRepository.countByItemTypeAndItemId(ItemType.POLICY, policy.getPolicyId());
                    boolean isScrap = true;
                    return PolicyListResponseDto.toListDto(policy, isScrap, scrapCount);
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
                    long scrapCount = scrapRepository.countByItemTypeAndItemId(ItemType.POLICY, policy.getPolicyId());
                    boolean isScrap = true;
                    return PolicyListResponseDto.toListDto(policy, isScrap, scrapCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 최근 본 정책 리스트 조회
     */
    @Override
    public List<PolicyListResponseDto> getRecentViewedPolicies(Member member) {
        List<RecentViewedPolicy> recentViewedPolicies = recentViewedPolicyRepository.findAllByMemberOrderByUpdatedAtDesc(member);

        return recentViewedPolicies.stream()
                .map(recentViewedPolicy -> {
                    long scrapCount = scrapRepository.countByItemTypeAndItemId(ItemType.POLICY, recentViewedPolicy.getPolicy().getPolicyId());
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), recentViewedPolicy.getPolicy().getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(recentViewedPolicy.getPolicy(), isScrap, scrapCount);
                })
                .toList();
    }

    /**
     * 최근 본 정책 삭제
     */
    @Override
    public void deleteRecentViewedPolicy(Member member) {
        recentViewedPolicyRepository.deleteAllByMember(member);
    }

    private void addRecentViewedPolicy(Member member, Policy policy) {
        Optional<RecentViewedPolicy> recentViewedPolicy = recentViewedPolicyRepository.findByMemberAndPolicy(member, policy);
        // 중복되면 최신 순서로 갱신
        if(recentViewedPolicy.isPresent()){
            recentViewedPolicy.get().setUpdatedAt();
        }
        else{
            recentViewedPolicyRepository.save(RecentViewedPolicy.builder().member(member).policy(policy).build());
            if(recentViewedPolicyRepository.countAllByMember(member) > 20){
                RecentViewedPolicy firstViewedPolicy = recentViewedPolicyRepository.findFirstByMemberOrderByUpdatedAt(member).get();
                recentViewedPolicyRepository.delete(firstViewedPolicy);
            }
        }
    }
}

