package com.server.youthtalktalk.response.error.user;

import com.server.youthtalktalk.response.error.BusinessException;
import com.server.youthtalktalk.response.error.ErrorCode;

public class UserAccessDeniedException extends BusinessException {
    public UserAccessDeniedException() {
        super(ErrorCode.USER_ACCESS_DENIED);
    }
}
