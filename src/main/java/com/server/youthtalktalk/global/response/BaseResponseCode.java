package com.server.youthtalktalk.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseCode {
    /* 요청 성공 시*/
    //공통
    SUCCESS("S01","요청에 성공하였습니다.",HttpStatus.OK.value()),
    // Scrap
    SUCCESS_SCRAP("S02","스크랩에 성공하였습니다.",HttpStatus.OK.value()),
    SUCCESS_SCRAP_CANCEL("S03","스크랩을 취소하였습니다",HttpStatus.OK.value()),
    // Policy
    SUCCESS_POLICY_FOUND("S04","정책 조회에 성공하였습니다.",HttpStatus.OK.value()),
    SUCCESS_POLICY_SEARCH_NO_RESULT("S05","조건에 맞는 정책 결과가 없습니다",HttpStatus.OK.value()),
    // Comment
    SUCCESS_COMMENT_CREATE("S06", "댓글을 성공적으로 등록했습니다.",HttpStatus.OK.value()),
    SUCCESS_COMMENT_UPDATE("S07", "댓글을 성공적으로 수정했습니다.",HttpStatus.OK.value()),
    SUCCESS_COMMENT_DELETE("S08", "댓글을 성공적으로 삭제했습니다.",HttpStatus.OK.value()),
    SUCCESS_COMMENT_EMPTY("S09", "해당하는 댓글이 없습니다.",HttpStatus.OK.value()),
    SUCCESS_COMMENT_LIKED("S10", "좋아요 등록이 완료되었습니다.",HttpStatus.OK.value()),
    SUCCESS_COMMENT_UNLIKED("S11", "좋아요 해제가 완료되었습니다.",HttpStatus.OK.value()),
    // Member
    SUCCESS_MEMBER_UPDATE("S12", "회원정보 수정을 완료하였습니다.", HttpStatus.OK.value()),
    SUCCESS_MEMBER_DELETE("S13", "회원 탈퇴를 완료하였습니다.", HttpStatus.OK.value()),
    SUCCESS_MEMBER_BLOCK("S14", "사용자를 성공적으로 차단했습니다.", HttpStatus.OK.value()),
    // Announcement
    SUCCESS_ANNOUNCEMENT_CREATE("S15", "공지사항을 성공적으로 등록했습니다.", HttpStatus.OK.value()),
    SUCCESS_ANNOUNCEMENT_UPDATE("S16", "공지사항을 성공적으로 수정했습니다.", HttpStatus.OK.value()),
    SUCCESS_ANNOUNCEMENT_DELETE("S17", "공지사항을 성공적으로 삭제했습니다.", HttpStatus.OK.value()),

    /* 요청 실패 시*/
    //공통
    INVALID_INPUT_VALUE("F01", "유효하지 않은 값을 입력하였습니다.", HttpStatus.BAD_REQUEST.value()),
    METHOD_NOT_ALLOWED("F02", "허용되지 않는 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    ENTITY_NOT_FOUND("F03", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    INTERNAL_SERVER_ERROR("F04", "예기치 못한 서버 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    // Member
    MEMBER_ACCESS_DENIED("M01", "회원이 아닙니다.", HttpStatus.UNAUTHORIZED.value()),
    MEMBER_NOT_FOUND("M02", "사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    APPLE_PUBLIC_KEY_ERROR("M03","애플 공개키를 이용한 서명 검증에 실패했습니다.",HttpStatus.BAD_REQUEST.value()),
    APPLE_TOKEN_VALIDATION_ERROR("MO4","애플 토큰이 유효하지 않습니다.",HttpStatus.BAD_REQUEST.value()),
    APPLE_NEED_SIGN_UP("M05","애플 최초 회원가입이 필요합니다.",HttpStatus.BAD_REQUEST.value()),
    APPLE_NEED_ADD_SIGN_UP("M06","애플 추가 회원가입이 필요합니다.",HttpStatus.BAD_REQUEST.value()),
    MEMBER_DUPLICATED("M07", "이미 가입한 회원입니다.", HttpStatus.BAD_REQUEST.value()),
    APPLE_USER_IDENTIFIER_ERROR("M08","애플 USERIDENTIFIER가 유효하지 않습니다.",HttpStatus.BAD_REQUEST.value()),
    BLOCK_DUPLICATED("M09", "이미 차단한 회원입니다.", HttpStatus.BAD_REQUEST.value()),

    // Policy
    POLICY_NOT_FOUND("PC01","해당 정책을 찾을 수 없습니다.",HttpStatus.BAD_REQUEST.value()),

    // Post
    POST_NOT_FOUND("PS01","해당 게시글을 찾을 수 없습니다.",HttpStatus.BAD_REQUEST.value()),
    POST_ACCESS_DENIED("PS02","해당 게시글에 대한 권한이 없습니다.",HttpStatus.BAD_REQUEST.value()),

    // Comment
    COMMENT_NOT_FOUND("C01", "해당 댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST.value()),
    COMMENT_ALREADY_LIKED("C02", "이미 좋아요한 댓글입니다.", HttpStatus.BAD_REQUEST.value()),
    COMMENT_LIKE_NOT_FOUND("C03", "좋아요 정보가 없습니다.", HttpStatus.BAD_REQUEST.value()),

    // Token
    INVALID_ACCESS_TOKEN("T01", "유효하지 않은 엑세스 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),
    INVALID_REFRESH_TOKEN("T02", "유효하지 않은 리프레쉬 토큰입니다.", HttpStatus.UNAUTHORIZED.value()),

    // Announcement
    ANNOUNCEMENT_NOT_FOUND_EXCEPTION("A01","해당 공지사항을 찾을 수 없습니다.",HttpStatus.NOT_FOUND.value());

    private final String code;
    private final String message;
    private final int status;

    BaseResponseCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
