package com.server.youthtalktalk.dto.comment;

public record PolicyCommentDto(Long commentId, String nickname, String content, String policyId) implements MyCommentDto{
}
