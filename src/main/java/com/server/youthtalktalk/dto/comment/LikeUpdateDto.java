package com.server.youthtalktalk.dto.comment;

import jakarta.validation.constraints.NotNull;

public record LikeUpdateDto(@NotNull Long commentId, boolean isSetLiked) {
}
