package com.server.youthtalktalk.global.response.exception;

import com.server.youthtalktalk.global.response.BaseResponseCode;

public class InvalidValueException extends BusinessException {
    
    public InvalidValueException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode);
    }
}
