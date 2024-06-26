package com.server.youthtalktalk.global.response.error.member;

import com.server.youthtalktalk.global.response.error.BusinessException;
import com.server.youthtalktalk.global.response.error.ErrorCode;

public class MemberAccessDeniedException extends BusinessException {
    public MemberAccessDeniedException() {
        super(ErrorCode.MEMBER_ACCESS_DENIED);
    }
}
