package com.server.youthtalktalk.global.response.exception.announcement;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;

public class AnnouncementNotFoundException extends EntityNotFoundException {

    public AnnouncementNotFoundException() {
        super(BaseResponseCode.ANNOUNCEMENT_NOT_FOUND_EXCEPTION);
    }
}
