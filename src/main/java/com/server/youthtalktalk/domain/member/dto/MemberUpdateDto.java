package com.server.youthtalktalk.domain.member.dto;

import static com.server.youthtalktalk.domain.member.controller.MemberController.NICKNAME_REGEX;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateDto(
        @Size(max = 8, message = "닉네임 길이는 1자 이상 8자 이하여야 합니다.")
        @Pattern(
                regexp = NICKNAME_REGEX,
                message = "닉네임은 한글과 영어(대소문자) 및 숫자만 가능하며, 공백과 특수문자는 사용할 수 없습니다."
        )
        String nickname,
        @Pattern(regexp = "서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주|세종",
                 message = "지역이 유효하지 않습니다.")
        String region
) {
}
