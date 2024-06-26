package com.server.youthtalktalk.global.response.error;

public class InvalidValueException extends BusinessException {
    
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
