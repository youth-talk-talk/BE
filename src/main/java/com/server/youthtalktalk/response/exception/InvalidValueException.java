package com.server.youthtalktalk.response.exception;

import com.server.youthtalktalk.response.BaseResponseCode;

public class InvalidValueException extends BusinessException {
    
    public InvalidValueException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode);
    }
}
