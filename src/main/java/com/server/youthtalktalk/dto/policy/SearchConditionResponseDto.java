package com.server.youthtalktalk.dto.policy;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
