package com.server.youthtalktalk.domain.comment.controller;

import com.server.youthtalktalk.domain.comment.dto.*;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.domain.comment.service.CommentService;
import com.server.youthtalktalk.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    /**
     * 정책 댓글 등록 api
     */
    @PostMapping("/policies/comments")
    public BaseResponse<Map<String, Long>> createPolicyComment(@Valid @RequestBody PolicyCommentCreateDto policyCommentCreateDto) {
        PolicyComment policyComment = commentService.createPolicyComment(policyCommentCreateDto.policyId(), policyCommentCreateDto.content(), memberService.getCurrentMember());
        Map<String, Long> response = new HashMap<>();
        response.put("commentId", policyComment.getId());
        return new BaseResponse<>(response, SUCCESS_COMMENT_CREATE);
    }

    /**
     * 게시글 댓글 등록 api
     */
    @PostMapping("/posts/comments")
    public BaseResponse<Map<String, Long>> createPostComment(@Valid @RequestBody PostCommentCreateDto postCommentCreateDto) {
        PostComment postComment = commentService.createPostComment(postCommentCreateDto.postId(), postCommentCreateDto.content(), memberService.getCurrentMember());
        Map<String, Long> response = new HashMap<>();
        response.put("commentId", postComment.getId());
        return new BaseResponse<>(response, SUCCESS_COMMENT_CREATE);
    }

    /**
     * 정책 댓글 조회 api
     */
    @GetMapping("/policies/{policyId}/comments")
    public BaseResponse<List<CommentDto>> getPolicyComments(@PathVariable String policyId) {
        List<PolicyComment> policyComments = commentService.getPolicyComments(policyId);
        List<CommentDto> commentDtoList = commentService.toCommentDtoList(policyComments, memberService.getCurrentMember());
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 게시글 댓글 조회 api
     */
    @GetMapping("/posts/{postId}/comments")
    public BaseResponse<List<CommentDto>> getPostComments(@PathVariable Long postId) {
        List<PostComment> postComments = commentService.getPostComments(postId);
        List<CommentDto> commentDtoList = commentService.toCommentDtoList(postComments, memberService.getCurrentMember());
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 회원이 작성한 댓글 조회 api
     */
    @GetMapping("/members/me/comments")
    public BaseResponse<List<MyCommentDto>> getMemberComments() {
        Member member = memberService.getCurrentMember();
        List<Comment> comments = commentService.getMyComments(member);

        if (comments.isEmpty()) // 회원이 작성한 댓글이 없는 경우
            return new BaseResponse<>(SUCCESS_COMMENT_EMPTY);

        List<MyCommentDto> myCommentDtoList = commentService.toMyCommentDtoList(comments, member.getNickname());
        return new BaseResponse<>(myCommentDtoList, SUCCESS);
    }

    /**
     * 회원이 좋아요한 댓글 조회 api
     */
    @GetMapping("/members/me/comments/likes")
    public BaseResponse<List<MyCommentDto>> getLikedComments() {
        Member member = memberService.getCurrentMember();
        List<Comment> likedComments = commentService.getLikedComments(member);

        if(likedComments.isEmpty()) // 회원이 좋아요한 댓글이 없는 경우
            return new BaseResponse<>(SUCCESS_COMMENT_EMPTY);

        List<MyCommentDto> likedCommentDtoList = commentService.toMyCommentDtoList(likedComments, member.getNickname());
        return new BaseResponse<>(likedCommentDtoList, SUCCESS);
    }

    /**
     * 댓글 수정 api
     */
    @PatchMapping("/comments")
    public BaseResponse<Void> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        commentService.updateComment(commentUpdateDto.commentId(), commentUpdateDto.content());
        return new BaseResponse<>(SUCCESS_COMMENT_UPDATE);
    }

    /**
     * 댓글 삭제 api
     */
    @DeleteMapping("/comments/{commentId}")
    public BaseResponse<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new BaseResponse<>(SUCCESS_COMMENT_DELETE);
    }

    /**
     * 댓글 좋아요 등록/해제
     */
    @PostMapping("/comments/likes")
    public BaseResponse<Void> updateCommentLike(@Valid @RequestBody LikeUpdateDto likeUpdateDto) {
        Long commentId = likeUpdateDto.commentId();
        Member member = memberService.getCurrentMember();

        if (likeUpdateDto.isSetLiked()) {
            commentService.setCommentLiked(commentId, member);// 좋아요 등록
            return new BaseResponse<>(SUCCESS_COMMENT_LIKED);
        } else {
            commentService.setCommentUnliked(commentId, member); // 좋아요 해제
            return new BaseResponse<>(SUCCESS_COMMENT_UNLIKED);
        }
    }

}