package com.server.youthtalktalk.domain.member.entity;

public enum SocialType {
    KAKAO, APPLE;

    public static SocialType fromString(String socialType) {
        return SocialType.valueOf(socialType.toUpperCase());
    }
}
