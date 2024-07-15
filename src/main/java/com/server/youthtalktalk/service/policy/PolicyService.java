package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyService {


    public List<PolicyListResponseDto> getTop5Policies();
    public List<PolicyListResponseDto> getAllPolicies(Pageable pageable);
    public List<PolicyListResponseDto> getPoliciesByCategories(List<Category> categories, Pageable pageable);
    Scrap scrapPolicy(String policyId, Member member);
    List<PolicyListResponseDto> getScrapPolicies(Pageable pageable,Member member);




}
