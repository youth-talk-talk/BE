package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;

public class MemberDuplicatedException extends EntityNotFoundException {
    public MemberDuplicatedException() {
        super(BaseResponseCode.MEMBER_DUPLICATED);
    }
}
