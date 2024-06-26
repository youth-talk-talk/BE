package com.server.youthtalktalk.global.response.exception;

import com.server.youthtalktalk.global.response.BaseResponseCode;

public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode);
    }
}
