package com.server.youthtalktalk.controller.comment;

import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.dto.comment.CommentUpdateDto;
import com.server.youthtalktalk.dto.comment.PolicyCommentCreateDto;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.PostCommentCreateDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.comment.CommentService;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.server.youthtalktalk.global.response.BaseResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    /**
     * 정책 댓글 생성
     */
    @PostMapping("/policies/comments")
    public BaseResponse<CommentDto> createPolicyComment(@Valid @RequestBody PolicyCommentCreateDto policyCommentCreateDto) {
        PolicyComment policyComment = commentService.createPolicyComment(policyCommentCreateDto.policyId(), policyCommentCreateDto.content(), memberService.getCurrentMember());
        CommentDto commentDto = new CommentDto(policyComment.getWriter().getNickname(), policyComment.getContent());
        return new BaseResponse<>(commentDto, SUCCESS);
    }

    /**
     * 게시글 댓글 생성
     */
    @PostMapping("/posts/comments")
    public BaseResponse<CommentDto> createPostComment(@Valid @RequestBody PostCommentCreateDto postCommentCreateDto) {
        PostComment postComment = commentService.createPostComment(postCommentCreateDto.postId(), postCommentCreateDto.content(), memberService.getCurrentMember());
        CommentDto commentDto = new CommentDto(postComment.getWriter().getNickname(), postComment.getContent());
        return new BaseResponse<>(commentDto, SUCCESS);
    }

    /**
     * 정책 댓글 조회
     */
    @GetMapping("/policies/{policyId}/comments")
    public BaseResponse<List<CommentDto>> getPolicyComments(@NotBlank @PathVariable String policyId) {
        List<PolicyComment> policyComments = commentService.getPolicyComments(policyId);
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(policyComments);
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 게시글 댓글 조회
     */
    @GetMapping("/posts/{postId}/comments")
    public BaseResponse<List<CommentDto>> getPostComments(@NotNull @PathVariable Long postId) {
        List<PostComment> postComments = commentService.getPostComments(postId);
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(postComments);
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/comments")
    public BaseResponse<Void> updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        commentService.updateComment(commentUpdateDto.commentId(), commentUpdateDto.content());
        return new BaseResponse<>(BaseResponseCode.SUCCESS_COMMENT_UPDATE);
    }

    /**
     * 댓글 삭제
     */

}
