package com.server.youthtalktalk.dto.policy;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.global.util.DeadlineStatusCalculator;
import lombok.Data;

@Data
public class PolicyListResponseDto {

    private String policyId; // 정책 아이디
    private Category category; // 카테고리
    private String title; // 정책명
    private String deadlineStatus; // 마감 상태
    private String hostDep; // 주관 기관명
    private boolean isScrap; // 스크랩 여부

    public static PolicyListResponseDto toListDto(Policy policy, Boolean isScrap) {
        PolicyListResponseDto dto = new PolicyListResponseDto();
        dto.setPolicyId(policy.getPolicyId());
        dto.setCategory(policy.getCategory());
        dto.setTitle(policy.getTitle());
        dto.setDeadlineStatus(DeadlineStatusCalculator.calculateDeadline(policy.getApplyDue()));
        dto.setHostDep(policy.getHostDep());
        dto.setScrap(isScrap);
        return dto;
    }
}
