package com.server.youthtalktalk.dto.comment;

public record PolicyCommentDto(Long commentId, String content, String nickname, String policyId) implements MyCommentDto{
}
