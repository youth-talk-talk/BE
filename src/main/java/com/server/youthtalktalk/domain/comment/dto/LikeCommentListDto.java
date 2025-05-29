package com.server.youthtalktalk.domain.comment.dto;

import java.util.List;

public record LikeCommentListDto(
        int commentCount,
        List<LikeCommentDto> comments
) {
}
