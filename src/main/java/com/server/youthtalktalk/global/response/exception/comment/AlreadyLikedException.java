package com.server.youthtalktalk.global.response.exception.comment;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class AlreadyLikedException extends BusinessException {

    public AlreadyLikedException() {
        super(BaseResponseCode.COMMENT_ALREADY_LIKED);
    }
}
