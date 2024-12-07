package com.server.youthtalktalk.global.response.exception.member;

import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.EntityNotFoundException;

public class BlockDuplicatedException extends BusinessException {
    public BlockDuplicatedException() {
        super(BaseResponseCode.BLOCK_DUPLICATED);
    }
}
