package com.server.youthtalktalk.response.exception.member;

import com.server.youthtalktalk.response.exception.EntityNotFoundException;
import com.server.youthtalktalk.response.BaseResponseCode;

public class MemberNotFoundException extends EntityNotFoundException {

    public MemberNotFoundException() {
        super(BaseResponseCode.MEMBER_NOT_FOUND);
    }
}
