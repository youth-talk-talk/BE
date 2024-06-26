package com.server.youthtalktalk.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BaseResponse<T> {
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final int status;
    private final String message;
    private final String code;
    private T data;

    // 요청 성공한 경우
    public BaseResponse(T data,BaseResponseCode baseResponseCode) {
        this.status = baseResponseCode.getStatus();
        this.message = baseResponseCode.getMessage();
        this.code = baseResponseCode.getCode();
        this.data = data;
    }

    // 요청 실패한 경우
    public BaseResponse(BaseResponseCode baseResponseCode) {
        this.status = baseResponseCode.getStatus();
        this.message = baseResponseCode.getMessage();
        this.code = baseResponseCode.getCode();
    }
}
