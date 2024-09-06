package com.server.youthtalktalk.dto.comment;

public record PostCommentDto(Long commentId, String content, String nickname, Long postId) implements MyCommentDto {
}
