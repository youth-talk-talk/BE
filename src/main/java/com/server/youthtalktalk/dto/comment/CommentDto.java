package com.server.youthtalktalk.dto.comment;

public record CommentDto (
        Long commentId,
        String content,
        String nickname,
        Boolean isLikedByMember) {
}
