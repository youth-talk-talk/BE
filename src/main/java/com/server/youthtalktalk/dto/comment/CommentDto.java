package com.server.youthtalktalk.dto.comment;

public record CommentDto(
        Long commentId, String nickname, String content, boolean isLikedByMember, Object relatedEntityId) {
}
