package com.server.youthtalktalk.domain.comment.dto;

import java.util.List;

public record MyCommentListDto(
        int commentCount,
        List<MyCommentDto> comments
) {
}
