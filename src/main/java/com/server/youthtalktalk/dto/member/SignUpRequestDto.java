package com.server.youthtalktalk.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class SignUpRequestDto {

    @NotNull(message = "username은 필수값입니다.")
    private String username; // 소셜타입+id

    @NotNull(message = "닉네임은 필수값입니다.")
    @Size(max = 8, message = "닉네임 길이는 8자 이하입니다.")
    private String nickname;

    @NotNull(message = "지역은 필수값입니다.")
    @Pattern(regexp = "서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주|세종", message = "지역이 유효하지 않습니다.")
    private String region;

    private String idToken;

}
