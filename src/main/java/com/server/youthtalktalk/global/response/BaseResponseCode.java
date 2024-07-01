package com.server.youthtalktalk.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseCode {
    /* 요청 성공 시*/
    //공통
    SUCCESS("S01","요청에 성공하였습니다.",HttpStatus.OK.value()),

    /* 요청 실패 시*/
    //공통
    INVALID_INPUT_VALUE("F01", "유효하지 않은 값을 입력하였습니다.", HttpStatus.BAD_REQUEST.value()),
    METHOD_NOT_ALLOWED("F02", "허용되지 않는 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    ENTITY_NOT_FOUND("F03", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    INTERNAL_SERVER_ERROR("F04", "예기치 못한 서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    //Member
    MEMBER_ACCESS_DENIED("M01", "인증되지 않은 사용자의 접근입니다.", HttpStatus.UNAUTHORIZED.value()),
    MEMBER_NOT_FOUND("M02", "사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value());

    private final String code;
    private final String message;
    private final int status;

    BaseResponseCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
