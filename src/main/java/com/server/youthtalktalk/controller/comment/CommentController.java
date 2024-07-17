package com.server.youthtalktalk.controller.comment;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        CommentDto comment;

        if (commentCreateDto.isPolicyComment()) {
            comment = commentService.createPolicyComment(commentCreateDto.policyId(), commentCreateDto.content(), member);
        } else if (commentCreateDto.isPostComment()) {
            comment = commentService.createPostComment(commentCreateDto.postId(), commentCreateDto.content(), member);
        } else {
            throw new CommentTypeException();
        }
        return new BaseResponse<>(comment, SUCCESS);
    }

    /**
     * 댓글 조회
     */
    @GetMapping("/comments")
    public BaseResponse<List<CommentDto>> getAllComments(@RequestBody CommentTypeDto commentTypeDto) {
        List<CommentDto> commentDtoList = commentService.convertToDto(commentService.getAllComments(commentTypeDto)); // 작성자 없는 경우 null 처리 포함
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

    /**
     * 댓글 수정
     */

    /**
     * 댓글 삭제
     */

}
