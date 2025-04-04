package com.server.youthtalktalk.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyCommentCreateDto(
        @NotNull(message = "policyId는 필수값입니다.")
        Long policyId,
        @NotBlank(message = "content는 필수값입니다.")
        String content
) {
}