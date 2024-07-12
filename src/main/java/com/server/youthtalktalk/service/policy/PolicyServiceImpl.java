package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.dto.policy.PolicyDetailResponseDto;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import com.server.youthtalktalk.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        try {
            memberId = memberService.getCurrentMember().getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<Policy> policies = policyRepository.findTop5ByOrderByViewDesc();
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
     * 모든 정책 조회
     */
    @Override
    public List<PolicyListResponseDto> getAllPolicies(Pageable pageable) {
        Long memberId;
        try {
            memberId = memberService.getCurrentMember().getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<Policy> policies = policyRepository.findAll(pageable).getContent();
        if (policies.isEmpty()) {
            throw new PolicyNotFoundException();
        }

        List<PolicyListResponseDto> result = policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
        log.info("모든 정책 조회 성공");
        return result;
    }


    /**
     * 카테고리 별 정책 조회
     */
    @Override
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable) {
        Long memberId;
        try {
            memberId = memberService.getCurrentMember().getId();
        } catch (Exception e) {
            throw new MemberNotFoundException();
        }

        List<Policy> policies = policyRepository.findByCategoryIn(categories, pageable).getContent();
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

        boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
        PolicyDetailResponseDto result = PolicyDetailResponseDto.toDto(policy, isScrap);
        log.info("특정 정책 세부 조회 성공");
        return result;
    }


}

