package com.server.youthtalktalk.domain.member.dto.apple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppleTokenResponseDto(
        @JsonProperty(value = "access_token")
        String accessToken, // 애플 액세스 토큰

        @JsonProperty(value = "expires_in")
        String expiresIn, // 애플 토큰 만료 기한

        @JsonProperty(value = "id_token")
        String idToken, // 애플 idToken

        @JsonProperty(value = "refresh_token")
        String refreshToken, // 애플 리프레시 토큰

        @JsonProperty(value = "token_type")
        String tokenType, // 애플 tokenType

        String error
) {
}
