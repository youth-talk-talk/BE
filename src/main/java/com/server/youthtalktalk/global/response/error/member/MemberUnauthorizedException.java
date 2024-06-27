package com.server.youthtalktalk.global.response.error.member;

import com.server.youthtalktalk.global.response.error.BusinessException;
import com.server.youthtalktalk.global.response.error.ErrorCode;

public class MemberUnauthorizedException extends BusinessException {
    public MemberUnauthorizedException() {
        super(ErrorCode.MEMBER_UNAUTHORIZED);
    }
}
