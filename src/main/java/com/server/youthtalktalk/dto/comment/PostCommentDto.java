package com.server.youthtalktalk.dto.comment;

public record PostCommentDto(Long commentId, String nickname, String content, Long postId) implements MyCommentDto {
}
