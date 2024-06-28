package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class AppleNeedAddSignUpException extends BusinessException {
    public AppleNeedAddSignUpException() {
        super(BaseResponseCode.APPLE_NEED_ADD_SIGN_UP);
    }
}
