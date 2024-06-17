package com.server.youthtalktalk.response.error;

public class InvalidValueException extends BusinessException {
    
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
