package com.server.youthtalktalk.response.exception;

import com.server.youthtalktalk.response.BaseResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BaseResponseCode baseResponseCode;
    
    public BusinessException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode.getMessage());
        this.baseResponseCode = baseResponseCode;
    }
}
