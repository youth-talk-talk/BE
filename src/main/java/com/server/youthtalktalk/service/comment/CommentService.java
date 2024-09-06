package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.MyCommentDto;
import com.server.youthtalktalk.dto.comment.PolicyCommentDto;
import com.server.youthtalktalk.dto.comment.PostCommentDto;

import java.util.List;

public interface CommentService {
    PolicyComment createPolicyComment(String policyId, String content, Member member);
    PostComment createPostComment(Long postId, String content, Member member);
    List<PolicyComment> getPolicyComments(String policyId);
    List<PostComment> getPostComments(Long postId);
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
