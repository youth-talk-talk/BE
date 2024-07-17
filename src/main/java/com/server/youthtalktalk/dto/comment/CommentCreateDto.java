package com.server.youthtalktalk.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
        Long postId,
        String policyId,
        @NotBlank(message = "content는 필수값입니다.")
        String content
) {
    public boolean isPolicyComment() {
        return (policyId != null && postId == null);
    }

    public boolean isPostComment() {
        return (policyId == null && postId != null);
    }
}