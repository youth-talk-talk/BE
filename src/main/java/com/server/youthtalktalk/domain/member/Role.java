package com.server.youthtalktalk.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"), // 소셜로그인 후 회원가입하지 않은 사용자
    MEMBER("ROLE_MEMBER"), // 회원가입 완료한 사용자
    ADMIN("ROLE_ADMIN"); // 관리자

    private final String key;
}
