package com.server.youthtalktalk.global.response.exception.post;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;

public class BlockedMemberPostAccessDeniedException extends BusinessException {
    public BlockedMemberPostAccessDeniedException() {
        super(BaseResponseCode.BLOCKED_MEMBER_POST_ACCESS_DENIED);
    }
}
