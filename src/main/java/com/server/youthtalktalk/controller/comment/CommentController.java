package com.server.youthtalktalk.controller.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.*;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.repository.LikeRepository;
import com.server.youthtalktalk.service.comment.CommentService;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;
    private final LikeRepository likeRepository;

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
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(policyComments, memberService.getCurrentMember());
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 게시글 댓글 조회 api
     */
    @GetMapping("/posts/{postId}/comments")
    public BaseResponse<List<CommentDto>> getPostComments(@PathVariable Long postId) {
        List<PostComment> postComments = commentService.getPostComments(postId);
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(postComments, memberService.getCurrentMember());
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 회원이 작성한 댓글 조회 api
     */
    @GetMapping("/members/me/comments")
    public BaseResponse<List<MyCommentDto>> getMemberComments() {
        Member member = memberService.getCurrentMember();
        List<Comment> comments = commentService.getMemberComments(member);

        if (comments.isEmpty()) // 회원이 작성한 댓글이 없는 경우
            return new BaseResponse<>(SUCCESS_COMMENT_EMPTY);

        List<MyCommentDto> commentDtoList = comments.stream()
                .map(comment -> new MyCommentDto(comment.getId(), comment.getContent()))
                .collect(Collectors.toList());

        return new BaseResponse<>(commentDtoList, SUCCESS);
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

    @GetMapping("/members/me/comments/likes")
    public BaseResponse<List<MyCommentDto>> getLikedComments() {
        List<Comment> likedComments = commentService.getLikedComments(memberService.getCurrentMember());
        List<MyCommentDto> commentDtoList = likedComments.stream().map(comment -> new MyCommentDto(comment.getId(), comment.getContent()))
                .collect(Collectors.toList());
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

}
