package com.server.youthtalktalk.response.error;

public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
