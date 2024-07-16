package com.server.youthtalktalk.controller.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.server.youthtalktalk.global.response.BaseResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public BaseResponse<List<CommentDto>> getAllComments(@RequestBody CommentTypeDto commentTypeDto) {
        List<Comment> commentList = commentService.getAllComments(commentTypeDto);
        List<CommentDto> commentDtoList = commentService.convertToDto(commentList); // 작성자 없는 경우 null 처리 포함
        return new BaseResponse<>(commentDtoList, SUCCESS);
    }

}
