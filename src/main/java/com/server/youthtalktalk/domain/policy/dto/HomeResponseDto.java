package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.post.dto.PostListRepDto.PostListDto;
import java.util.List;

public record HomeResponseDto(
        List<PolicyListResponseDto> popularPolicies, // 우리 지역 인기 정책
        NewPoliciesResponseDto newPolicies, // 따끈따끈 새로운 정책
        List<PolicyWithReviewsDto> policiesWithReviews, // 지금뜨는 정책톡톡
        List<PostListDto> bestPosts // 청년톡톡 BEST
) {
}
