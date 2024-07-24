package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    PolicyComment createPolicyComment(String policyId, String content, Member member);
    PostComment createPostComment(Long postId, String content, Member member);
    List<PolicyComment> getPolicyComments(String policyId);
    List<PostComment> getPostComments(Long postId);
    List<Comment> getMemberComments(Member member);
    List<CommentDto> convertToCommentDtoList(List<? extends Comment> comments, Member member);
    void updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
    boolean isLikedByMember(Comment comment, Member member);
}
