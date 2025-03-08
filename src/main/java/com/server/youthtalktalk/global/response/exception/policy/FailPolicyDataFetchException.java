package com.server.youthtalktalk.global.response.exception.policy;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class FailPolicyDataFetchException extends BusinessException {
    public FailPolicyDataFetchException() {
        super(BaseResponseCode.FAIL_POLICY_DATA_FETCH);
    }
}
