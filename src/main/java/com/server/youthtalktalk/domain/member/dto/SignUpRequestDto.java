package com.server.youthtalktalk.domain.member.dto;

import static com.server.youthtalktalk.domain.member.controller.MemberController.NICKNAME_REGEX;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpRequestDto {

    @NotBlank(message = "socialId는 필수값입니다.")
    private String socialId;

    @NotBlank(message = "socialType은 필수값입니다.")
    private String socialType;

    @NotBlank(message = "닉네임은 필수값입니다.")
    @Pattern(
            regexp = NICKNAME_REGEX,
            message = "닉네임은 한글과 영어로 1자 이상 8자 이하이며, 공백과 특수문자는 사용할 수 없습니다."
    )
    private String nickname;

    @NotBlank(message = "지역은 필수값입니다.")
    @Pattern(regexp = "서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주|세종", message = "지역이 유효하지 않습니다.")
    private String region;

    private String idToken;

}
