package com.server.youthtalktalk.dto.policy;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchNameResponseDto {
    private String title;
    private String policyId;
}
