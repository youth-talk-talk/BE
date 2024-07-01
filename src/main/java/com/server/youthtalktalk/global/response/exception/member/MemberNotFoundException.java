package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;
import com.server.youthtalktalk.global.response.BaseResponseCode;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException() {
        super(BaseResponseCode.MEMBER_NOT_FOUND);
    }
}
