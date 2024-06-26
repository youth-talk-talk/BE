package com.server.youthtalktalk.global.response.error.user;

import com.server.youthtalktalk.global.response.error.EntityNotFoundException;
import com.server.youthtalktalk.global.response.error.ErrorCode;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
