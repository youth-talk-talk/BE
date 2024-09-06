package com.server.youthtalktalk.dto.announcement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnnouncementDto(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 50, message = "제목은 50자 이내여야 합니다.")
        String title,
        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}
