package com.server.youthtalktalk.response.exception;

import com.server.youthtalktalk.response.BaseResponseCode;

public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode);
    }
}
