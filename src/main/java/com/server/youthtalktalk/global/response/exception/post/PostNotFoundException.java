package com.server.youthtalktalk.global.response.exception.post;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class PostNotFoundException extends BusinessException {
    public PostNotFoundException() {
        super(BaseResponseCode.POST_NOT_FOUND);
    }
}
