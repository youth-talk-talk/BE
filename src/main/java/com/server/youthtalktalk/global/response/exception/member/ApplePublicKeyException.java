package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class ApplePublicKeyException extends BusinessException {
    public ApplePublicKeyException() {
        super(BaseResponseCode.APPLE_PUBLIC_KEY_ERROR);
    }
}
