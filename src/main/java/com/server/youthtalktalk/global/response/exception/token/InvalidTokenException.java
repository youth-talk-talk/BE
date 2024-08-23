package com.server.youthtalktalk.global.response.exception.token;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final BaseResponseCode baseResponseCode;

    public InvalidTokenException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode.getMessage());
        this.baseResponseCode = baseResponseCode;
    }
}
