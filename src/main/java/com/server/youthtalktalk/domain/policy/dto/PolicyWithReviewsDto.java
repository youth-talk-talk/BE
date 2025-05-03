package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;

public record PolicyWithReviewsDto(
        Long policyId,
        String title,
        String imgUrl,
        List<ReviewInPolicyDto> reviews
) {
}
