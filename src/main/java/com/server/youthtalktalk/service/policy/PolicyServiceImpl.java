package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.policy.PolicyDetailResponseDto;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.dto.policy.SearchConditionRequestDto;
import com.server.youthtalktalk.dto.policy.SearchConditionResponseDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import com.server.youthtalktalk.service.member.MemberService;
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

    private final PolicyRepository policyRepository;
    private final ScrapRepository scrapRepository;
    private final MemberService memberService;

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
    public SearchConditionResponseDto getPoliciesByCondition(SearchConditionRequestDto condition, Pageable pageable) {
        Long memberId;
        Region region;
        try {
            memberId = memberService.getCurrentMember().getId();
            region = memberService.getCurrentMember().getRegion();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<String> employmentCodes = null;
        if (condition.getEmploymentCodeList() != null) {
            employmentCodes = condition.getEmploymentCodeList().stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
        }

        log.info("Filtering policies with conditions: categories={}, age={}, employmentCodes={}, isFinished={}, keyword={}", condition.getCategories(), condition.getAge(), employmentCodes, condition.getIsFinished(), condition.getKeyword());

        Page<Policy> policies = policyRepository.findByCondition(
                region,
                condition.getCategories(),
                condition.getAge(),
                employmentCodes,
                condition.getIsFinished(),
                condition.getKeyword(),
                pageable
        );

        log.info("Filtered policies count: {}", policies.getTotalElements());

        if (policies.isEmpty()) {
            log.info("조건에 맞는 정책이 존재하지 않습니다");
            return SearchConditionResponseDto.toListDto(Collections.emptyList(), 0L); // 빈 리스트 반환
        }

        List<PolicyListResponseDto> result = policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
        log.info("조건 적용 정책 조회 성공");
        return SearchConditionResponseDto.toListDto(result, policies.getTotalElements());
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

}

