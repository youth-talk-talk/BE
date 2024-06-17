package com.server.youthtalktalk.response.error.user;

import com.server.youthtalktalk.response.error.EntityNotFoundException;
import com.server.youthtalktalk.response.error.ErrorCode;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
