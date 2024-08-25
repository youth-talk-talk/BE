package com.server.youthtalktalk.dto.member.apple;

import com.server.youthtalktalk.domain.member.SocialType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
public class AppleDto {
    @Getter
    public static class AppleCodeRequestDto {
        private String userIdentifier;
        private String identityToken;
        private String authorizationCode;
    }

    @Getter
    @Builder
    public static class AppleLoginTestDto {
        private String email;
        private SocialType socialType;
        private String AppleAccessToken;
        private String AppleRefreshToken;
        private String id;
    }

    @Getter
    @Builder
    @ToString
    public static class AppleTokenRequestDto {
        private String client_id;
        private String client_secret;
        private String code;
        private String grant_type;
        private String refresh_token;
    }

    @Getter
    @Builder
    public static class AppleRevokeRequest {
        private String client_id;
        private String client_secret;
        private String token;
        private String token_type_hint;
    }
}
