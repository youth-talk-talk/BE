package com.server.youthtalktalk.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCommentCreateDto(
        @NotNull(message = "postId는 필수값입니다.")
        Long postId,
        @NotBlank(message = "content는 필수값입니다.")
        String content
) {
}