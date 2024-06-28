package com.server.youthtalktalk.dto.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
}
