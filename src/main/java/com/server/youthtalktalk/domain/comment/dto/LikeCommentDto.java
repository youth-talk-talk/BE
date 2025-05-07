package com.server.youthtalktalk.domain.comment.dto;

public record LikeCommentDto(
        Long commentId,
        Long writerId,
        String nickname,
        String content,
        Long articleId,
        String articleType,
        String articleTitle,
        Boolean isLikedByMember,
        int likeCount
) {
}
