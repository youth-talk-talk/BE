package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import org.springframework.security.core.AuthenticationException;

public class MemberAccessDeniedException extends BusinessException {
    public MemberAccessDeniedException() {
        super(BaseResponseCode.MEMBER_ACCESS_DENIED);
    }
}
