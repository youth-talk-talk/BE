package com.server.youthtalktalk.global.response.exception.post;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class ReportedPostAccessDeniedException extends BusinessException {
    public ReportedPostAccessDeniedException() {
        super(BaseResponseCode.REPORTED_POST_ACCESS_DENIED);
    }
}
