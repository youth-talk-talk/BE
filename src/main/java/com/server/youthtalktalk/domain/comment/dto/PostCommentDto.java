package com.server.youthtalktalk.domain.comment.dto;

public record PostCommentDto(Long commentId, String nickname, String content, Long postId) implements MyCommentDto {
}
