package com.server.youthtalktalk.global.response.error.user;

import com.server.youthtalktalk.global.response.error.BusinessException;
import com.server.youthtalktalk.global.response.error.ErrorCode;

public class UserAccessDeniedException extends BusinessException {
    public UserAccessDeniedException() {
        super(ErrorCode.USER_ACCESS_DENIED);
    }
}
