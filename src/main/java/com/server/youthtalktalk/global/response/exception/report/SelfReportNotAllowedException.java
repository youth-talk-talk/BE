package com.server.youthtalktalk.global.response.exception.report;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class SelfReportNotAllowedException extends BusinessException {
    public SelfReportNotAllowedException() {
        super(BaseResponseCode.SELF_REPORT_NOT_ALLOWED_EXCEPTION);
    }
}
