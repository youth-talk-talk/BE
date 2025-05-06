package com.server.youthtalktalk.domain.comment.service;

import com.server.youthtalktalk.domain.comment.dto.CommentDto;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.comment.dto.MyCommentDto;

import java.util.List;

public interface CommentService {
    PolicyComment createPolicyComment(Long policyId, String content, Member member);
    PostComment createPostComment(Long postId, String content, Member member);
    List<PolicyComment> getPolicyComments(Long policyId, Member member);
    List<PostComment> getPostComments(Long postId, Member member);
    List<Comment> getMyComments(Member member);
    List<Comment> getLikedComments(Member member);
    List<CommentDto> toCommentDtoList(List<? extends Comment> comments, Member member);
    List<MyCommentDto> toMyCommentDtoList(List<Comment> comments, String nickname);
    void updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
    boolean isLikedByMember(Comment comment, Member member);
    void setCommentLiked(Long commentId, Member member);
    void setCommentUnliked(Long commentId, Member member);
}
