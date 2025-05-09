package com.server.youthtalktalk.domain.policy.dto;

public record NewPoliciesResponseDto(
        PolicyPageDto all,
        PolicyPageDto job,
        PolicyPageDto dwelling,
        PolicyPageDto education,
        PolicyPageDto life,
        PolicyPageDto participation
) {
}
