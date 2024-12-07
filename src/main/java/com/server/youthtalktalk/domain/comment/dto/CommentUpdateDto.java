package com.server.youthtalktalk.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateDto(
        @NotNull Long commentId,
        @NotBlank String content
) {
}
