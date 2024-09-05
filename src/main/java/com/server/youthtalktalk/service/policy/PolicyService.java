package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.dto.policy.PolicyDetailResponseDto;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.dto.policy.SearchConditionRequestDto;
import com.server.youthtalktalk.dto.policy.SearchConditionResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyService {


    public List<PolicyListResponseDto> getTop5Policies();
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable);
    public SearchConditionResponseDto getPoliciesByCondition(SearchConditionRequestDto condition, Pageable pageable);
    public PolicyDetailResponseDto getPolicyDetail(String policyId);
    Scrap scrapPolicy(String policyId, Member member);
    List<PolicyListResponseDto> getScrapPolicies(Pageable pageable,Member member);

    List<PolicyListResponseDto> getScrappedPoliciesWithUpcomingDeadline(Member member);



}
