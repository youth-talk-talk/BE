package com.server.youthtalktalk.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberInfoDto {
    private final String nickname;
    private final String email;
    private final String region;
}
