package com.server.youthtalktalk.domain.comment.dto;

import java.util.List;

public record CommentListDto(
        int commentCount,
        List<CommentDto> comments
) {
}
