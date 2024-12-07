package com.server.youthtalktalk.global.response.exception.report;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class ReportAlreadyExistException extends BusinessException {
    public ReportAlreadyExistException() {
        super(BaseResponseCode.REPORT_ALREADY_EXISTENCE_EXCEPTION);
    }
}
