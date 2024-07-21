package com.server.youthtalktalk.global.response.exception.comment;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;

public class CommentNotFoundException extends EntityNotFoundException {

    public CommentNotFoundException() {
        super(BaseResponseCode.COMMENT_NOT_FOUND);
    }
}
