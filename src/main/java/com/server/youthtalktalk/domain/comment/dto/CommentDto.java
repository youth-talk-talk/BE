package com.server.youthtalktalk.domain.comment.dto;

public record CommentDto(
        Long commentId,
        Long writerId,
        String nickname,
        String profileImg,
        String content,
        Boolean isLikedByMember,
        String createdAt
) {
}
