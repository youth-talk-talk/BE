package com.server.youthtalktalk.global.response.error.member;

import com.server.youthtalktalk.global.response.error.EntityNotFoundException;
import com.server.youthtalktalk.global.response.error.ErrorCode;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
