package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class InvalidMemberForBlockException extends BusinessException {
    public InvalidMemberForBlockException() {
        super(BaseResponseCode.INVALID_MEMBER_FOR_BLOCK);
    }
}
