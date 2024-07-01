package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class AppleTokenValidationException extends BusinessException {
    public AppleTokenValidationException() {
        super(BaseResponseCode.APPLE_TOKEN_VALIDATION_ERROR);
    }
}
