package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import com.server.youthtalktalk.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Long memberId = memberService.getCurrentMember().getId();
        List<Policy> policies = policyRepository.findTop5ByOrderByViewDesc();
        return policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
    }

    /**
     * 모든 정책 조회
     */
    @Override
    public List<PolicyListResponseDto> getAllPolicies(Pageable pageable) {
        Long memberId = memberService.getCurrentMember().getId();
        List<Policy> policies = policyRepository.findAll(pageable).getContent();
        return policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable) {
        Long memberId = memberService.getCurrentMember().getId();
        List<Policy> policies = policyRepository.findByCategoryIn(categories, pageable).getContent();
        return policies.stream()
                .map(policy -> {
                    boolean isScrap = scrapRepository.existsByMemberIdAndItemIdAndItemType(memberId, policy.getPolicyId(), ItemType.POLICY);
                    return PolicyListResponseDto.toListDto(policy, isScrap);
                })
                .collect(Collectors.toList());
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

