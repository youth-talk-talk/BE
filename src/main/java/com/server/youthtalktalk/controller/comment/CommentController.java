package com.server.youthtalktalk.controller.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.*;
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
import java.util.stream.Collectors;

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
    public BaseResponse<CommentDto> createPolicyComment(@Valid @RequestBody PolicyCommentCreateDto policyCommentCreateDto) {
        PolicyComment policyComment = commentService.createPolicyComment(policyCommentCreateDto.policyId(), policyCommentCreateDto.content(), memberService.getCurrentMember());
        CommentDto commentDto = new CommentDto(policyComment.getId(), policyComment.getWriter().getNickname(), policyComment.getContent());
        return new BaseResponse<>(commentDto, SUCCESS);
    }

    /**
     * 게시글 댓글 등록 api
     */
    @PostMapping("/posts/comments")
    public BaseResponse<CommentDto> createPostComment(@Valid @RequestBody PostCommentCreateDto postCommentCreateDto) {
        PostComment postComment = commentService.createPostComment(postCommentCreateDto.postId(), postCommentCreateDto.content(), memberService.getCurrentMember());
        CommentDto commentDto = new CommentDto(postComment.getId(), postComment.getWriter().getNickname(), postComment.getContent());
        return new BaseResponse<>(commentDto, SUCCESS);
    }

    /**
     * 정책 댓글 조회 api
     */
    @GetMapping("/policies/{policyId}/comments")
    public BaseResponse<List<CommentDto>> getPolicyComments(@NotBlank @PathVariable String policyId) {
        List<PolicyComment> policyComments = commentService.getPolicyComments(policyId);
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(policyComments);
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 게시글 댓글 조회 api
     */
    @GetMapping("/posts/{postId}/comments")
    public BaseResponse<List<CommentDto>> getPostComments(@NotNull @PathVariable Long postId) {
        List<PostComment> postComments = commentService.getPostComments(postId);
        List<CommentDto> commentDtoList = commentService.convertToCommentDtoList(postComments);
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 회원이 작성한 댓글 조회 api
     */
    @GetMapping("/members/me/comments")
    public BaseResponse<List<CommentDto>> getMemberComments() {
        Member member = memberService.getCurrentMember();
        String nickname = member.getNickname();
        List<Comment> comments = commentService.getMemberComments(member);

        if (comments.isEmpty()) // 회원이 작성한 댓글이 없는 경우
            return new BaseResponse<>(SUCCESS_COMMENT_EMPTY);

        List<CommentDto> commentDtoList = comments.stream()
                .map(comment -> new CommentDto(comment.getId(), nickname, comment.getContent()))
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
    public BaseResponse<Void> deleteComment(@NotNull @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new BaseResponse<>(SUCCESS_COMMENT_DELETE);
    }

}
