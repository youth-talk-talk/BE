package com.server.youthtalktalk.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record PolicyCommentCreateDto(
        @NotBlank(message = "policyId는 필수값입니다.")
        String policyId,
        @NotBlank(message = "content는 필수값입니다.")
        String content
) {
}