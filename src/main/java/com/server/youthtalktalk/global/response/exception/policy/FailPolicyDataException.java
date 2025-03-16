package com.server.youthtalktalk.global.response.exception.policy;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class FailPolicyDataException extends BusinessException {
    public FailPolicyDataException(BaseResponseCode baseResponseCode) {
        super(baseResponseCode);
    }
}
