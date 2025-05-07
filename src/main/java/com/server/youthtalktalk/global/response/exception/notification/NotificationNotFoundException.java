package com.server.youthtalktalk.global.response.exception.notification;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class NotificationNotFoundException extends BusinessException {
    public NotificationNotFoundException() {
        super(BaseResponseCode.NOTIFICATION_NOT_FOUND_EXCEPTION);
    }
}
