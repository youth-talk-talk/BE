package com.server.youthtalktalk.domain.member;

public enum SocialType {
    KAKAO, APPLE;

    public static SocialType fromString(String socialType) {
        return SocialType.valueOf(socialType.toUpperCase());
    }
}
