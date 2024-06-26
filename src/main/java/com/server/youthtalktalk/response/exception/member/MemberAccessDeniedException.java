package com.server.youthtalktalk.response.exception.member;

import com.server.youthtalktalk.response.exception.BusinessException;
import com.server.youthtalktalk.response.BaseResponseCode;

public class MemberAccessDeniedException extends BusinessException {
    public MemberAccessDeniedException() {
        super(BaseResponseCode.MEMBER_ACCESS_DENIED);
    }
}
