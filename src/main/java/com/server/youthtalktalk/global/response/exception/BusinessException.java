package com.server.youthtalktalk.global.response.exception;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BaseResponseCode baseResponseCode;
    
    public BusinessException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode.getMessage());
        this.baseResponseCode = baseResponseCode;
    }
}
