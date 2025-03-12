package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchConditionResponseDto {
    Long totalCount;
    List<PolicyListResponseDto> policyList;

    public static SearchConditionResponseDto toListDto(List<PolicyListResponseDto> policyList, Long totalCount) {
        return SearchConditionResponseDto.builder()
                .totalCount(totalCount)
                .policyList(policyList)
                .build();
    }

}
