package com.server.youthtalktalk.dto.member;

import lombok.Getter;

/**
 * @param memberId 로그인 성공한 사용자의 회원 id
 */
public record LoginSuccessDto(Long memberId) {
}
