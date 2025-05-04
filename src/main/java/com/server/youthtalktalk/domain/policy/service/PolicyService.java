package com.server.youthtalktalk.domain.policy.service;

import com.server.youthtalktalk.domain.policy.entity.SortOption;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyService {
    List<PolicyListResponseDto> getTop20Policies();
    List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable);
    List<PolicyListResponseDto> getNewPoliciesByCategories(List<Category> categories);
    SearchConditionResponseDto getPoliciesByCondition(SearchConditionRequestDto condition, Pageable pageable, SortOption sortOption);
    List<SearchNameResponseDto> getPoliciesByName(String title, Pageable pageable);
    PolicyDetailResponseDto getPolicyDetail(Long policyId);
    Scrap scrapPolicy(Long policyId, Member member);
    List<PolicyListResponseDto> getScrapPolicies(Pageable pageable,Member member);
    List<PolicyListResponseDto> getScrappedPoliciesWithUpcomingDeadline(Member member);
    List<PolicyWithReviewsDto> getTop5PoliciesWithReviews(Member member);
}
