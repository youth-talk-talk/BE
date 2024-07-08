package com.server.youthtalktalk.dto.member;

import java.util.Optional;

public record MemberUpdateDto(Optional<String> nickname,
                              Optional<String> region) {
}
