package com.server.youthtalktalk.controller.comment;

import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.comment.CommentCreateDto;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.exception.comment.CommentTypeException;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.service.comment.CommentService;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.server.youthtalktalk.global.response.BaseResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    /**
     * 댓글 생성
     */
    @PostMapping("/comments")
    public BaseResponse<CommentDto> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto) {
        Member member = memberService.getCurrentMember();
        String policyId = commentCreateDto.policyId();
        Long postId = commentCreateDto.postId();
        String content = commentCreateDto.content();
        CommentDto commentDto;

        if (commentService.validateCommentType(policyId, postId)) {
            PolicyComment policyComment = commentService.createPolicyComment(policyId, content, member);
            commentDto = new CommentDto(policyComment.getWriter().getNickname(), policyComment.getContent());
        } else {
            PostComment postComment = commentService.createPostComment(postId, content, member);
            commentDto = new CommentDto(postComment.getWriter().getNickname(), postComment.getContent());
        }
        return new BaseResponse<>(commentDto, SUCCESS);
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/comments")
    public BaseResponse<List<CommentDto>> getComments(@RequestParam(required = false) String policyId,
                                                      @RequestParam(required = false) Long postId) {
        List<CommentDto> commentDtoList;
        if (commentService.validateCommentType(policyId, postId)) {
            commentDtoList = commentService.getPolicyComments(policyId);
        } else {
            commentDtoList = commentService.getPostComments(postId);
        }
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 댓글 수정
     */

    /**
     * 댓글 삭제
     */

}
