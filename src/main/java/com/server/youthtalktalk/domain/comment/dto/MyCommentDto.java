package com.server.youthtalktalk.domain.comment.dto;

public record MyCommentDto(
        Long commentId,
        String content,
        Long articleId,
        String articleType,
        String articleTitle,
        Boolean isLikedByMember,
        int likeCount
) {
}
