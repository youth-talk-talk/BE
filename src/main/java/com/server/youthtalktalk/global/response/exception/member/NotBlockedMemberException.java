package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class NotBlockedMemberException extends BusinessException {
    public NotBlockedMemberException() {
        super(BaseResponseCode.NOT_BLOCKED_MEMBER);
    }
}
