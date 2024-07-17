package com.server.youthtalktalk.global.response.exception.comment;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;

public class CommentTypeException extends EntityNotFoundException {

    public CommentTypeException() {
        super(BaseResponseCode.COMMENT_TYPE_NOT_CORRECT);
    }
}
