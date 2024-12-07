package com.server.youthtalktalk.domain.comment.dto;

public record CommentDto (
        Long commentId,
        String nickname,
        String content,
        Boolean isLikedByMember) {
}
