package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PolicyPageResponseDto {
    Long totalCount;
    List<PolicyListResponseDto> policyList;

    public static PolicyPageResponseDto toListDto(List<PolicyListResponseDto> policyList, Long totalCount) {
        return PolicyPageResponseDto.builder()
                .totalCount(totalCount)
                .policyList(policyList)
                .build();
    }
}
