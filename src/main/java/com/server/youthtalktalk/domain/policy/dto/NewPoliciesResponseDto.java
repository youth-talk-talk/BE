package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;

public record NewPoliciesResponseDto(
        List<PolicyListResponseDto> all,
        List<PolicyListResponseDto> job,
        List<PolicyListResponseDto> dwelling,
        List<PolicyListResponseDto> education,
        List<PolicyListResponseDto> life,
        List<PolicyListResponseDto> participation
) {
}
