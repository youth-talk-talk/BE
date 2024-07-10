package com.server.youthtalktalk.global.response.exception.policy;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class PolicyNotFoundException extends BusinessException {
    public PolicyNotFoundException() {
        super(BaseResponseCode.POLICY_NOT_FOUND);
    }
}

