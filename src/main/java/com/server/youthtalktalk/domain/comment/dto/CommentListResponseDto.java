package com.server.youthtalktalk.domain.comment.dto;

import java.util.List;

public record CommentListResponseDto(
        long commentCount,
        List<CommentDto> comments
) {
}
