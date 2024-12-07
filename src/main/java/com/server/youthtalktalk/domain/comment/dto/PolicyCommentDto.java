package com.server.youthtalktalk.domain.comment.dto;

public record PolicyCommentDto(Long commentId, String nickname, String content, String policyId) implements MyCommentDto{
}
