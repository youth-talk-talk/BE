package com.server.youthtalktalk.domain.comment.dto;

import jakarta.validation.constraints.NotNull;

public record LikeUpdateDto(@NotNull Long commentId, boolean isSetLiked) {
}
