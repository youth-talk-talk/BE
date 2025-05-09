package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;

public record HomeResponseDto(
        List<PolicyListResponseDto> popularPolicies // 우리 지역 인기 정책
) {
}
