package com.server.youthtalktalk.global.response.exception.comment;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class CommentLikeNotFoundException extends BusinessException {
    public CommentLikeNotFoundException() {
        super(BaseResponseCode.COMMENT_LIKE_NOT_FOUND);
    }
}
