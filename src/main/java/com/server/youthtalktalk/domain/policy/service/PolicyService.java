package com.server.youthtalktalk.domain.policy.service;

import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyService {


    public List<PolicyListResponseDto> getTop5Policies();
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable);
    public SearchConditionResponseDto getPoliciesByCondition(SearchConditionRequestDto condition, Pageable pageable);
    public List<SearchNameResponseDto> getPoliciesByName(String title, Pageable pageable);
    public PolicyDetailResponseDto getPolicyDetail(String policyId);
    Scrap scrapPolicy(String policyId, Member member);
    List<PolicyListResponseDto> getScrapPolicies(Pageable pageable,Member member);

    List<PolicyListResponseDto> getScrappedPoliciesWithUpcomingDeadline(Member member);



}
