package com.server.youthtalktalk.domain.member.dto;

public record MemberInfoDto(
        Long memberId,
        String nickname,
        String profileImgUrl,
        String region
) {
}
