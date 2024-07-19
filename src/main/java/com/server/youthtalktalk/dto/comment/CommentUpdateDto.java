package com.server.youthtalktalk.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateDto(
        @NotNull Long commentId,
        @NotBlank String content
) {
}
