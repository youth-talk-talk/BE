package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments(CommentTypeDto commentTypeDto);
    List<CommentDto> convertToDto(List<Comment> comments);
}
