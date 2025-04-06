package com.server.youthtalktalk.domain.policy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchNameResponseDto {
    private String title;
    private Long policyId;
}
