package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import java.util.List;
import org.springframework.data.domain.Page;

public record PolicyPageDto(
        int page, // 현재 페이지 번호
        int size, // 페이지당 항목 수
        long totalCount, // 전체 항목 수
        List<PolicyListResponseDto> policies // 현재 페이지의 정책 목록
) {
    public static PolicyPageDto from(Page<Policy> page, List<PolicyListResponseDto> content) {
        return new PolicyPageDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                content
        );
    }
}
