package com.server.youthtalktalk.domain.policy.dto;

import java.time.LocalDate;

public record ReviewInPolicyDto(
        Long postId,
        String title,
        String contentPreview,
        long commentCount,
        long scrapCount,
        String createdAt
) {
}
