package com.server.youthtalktalk.global.response.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@ToString
public class ErrorResponse {
    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private final int status;
    private final String message;
    private final String code;
    private final String path;

    public ErrorResponse(int status, String message, String code, String path) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.path = path;
    }

//    public static ErrorResponse of(HttpStatus status, String message, String path) {
//        return new ErrorResponse(status.value(), message, path, status.toString());
//    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getMessage(), errorCode.getCode(), path);
    }

    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
