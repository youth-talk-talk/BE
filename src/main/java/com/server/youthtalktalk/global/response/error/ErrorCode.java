package com.server.youthtalktalk.global.response.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //Common
    INVALID_INPUT_VALUE("C01", "Invalid Input Value.", HttpStatus.BAD_REQUEST.value()),
    METHOD_NOT_ALLOWED("C02", "Invalid Method Type.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    ENTITY_NOT_FOUND("C03", "Entity Not Found.", HttpStatus.NOT_FOUND.value()),
    INTERNAL_SERVER_ERROR("C04", "Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    FILE_NOT_UPLOAD("C05", "Internal Server Error.", HttpStatus.BAD_REQUEST.value()),

    //Member
    MEMBER_ACCESS_DENIED("M01", "Member Access is Denied.", HttpStatus.UNAUTHORIZED.value()),
    MEMBER_NOT_FOUND("M02", "Member is not Found.", HttpStatus.BAD_REQUEST.value());

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
