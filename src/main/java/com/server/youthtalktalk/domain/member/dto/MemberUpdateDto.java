package com.server.youthtalktalk.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateDto(
        @Size(max = 8, message = "닉네임 길이는 8자 이하입니다.")
        String nickname,
        @Pattern(regexp = "서울|부산|대구|인천|광주|대전|울산|경기|강원|충북|충남|전북|전남|경북|경남|제주|세종",
                 message = "지역이 유효하지 않습니다.")
        String region
) {
}
