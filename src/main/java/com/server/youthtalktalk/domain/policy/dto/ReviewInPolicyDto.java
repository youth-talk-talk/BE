package com.server.youthtalktalk.domain.policy.dto;

public record ReviewInPolicyDto(
        Long postId,
        String title,
        String contentPreview,
        long commentCount,
        long scrapCount,
        boolean scrap,
        String createdAt
) {
}
