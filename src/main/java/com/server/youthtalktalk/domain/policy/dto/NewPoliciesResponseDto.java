package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;

public record NewPoliciesResponseDto(
        List<PolicyListResponseDto> ALL,
        List<PolicyListResponseDto> JOB,
        List<PolicyListResponseDto> DWELLING,
        List<PolicyListResponseDto> EDUCATION,
        List<PolicyListResponseDto> LIFE,
        List<PolicyListResponseDto> PARTICIPATION
) {
}
