package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    PolicyComment createPolicyComment(String policyId, String content, Member member);
    PostComment createPostComment(Long postId, String content, Member member);
    List<CommentDto> getPolicyComments(String policyId);
    List<CommentDto> getPostComments(Long postId);
    boolean validateCommentType(String policyId, Long postId);
}
