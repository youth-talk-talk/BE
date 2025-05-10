package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.global.util.DeadlineStatusCalculator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PolicyListResponseDto {

    private Long policyId; // 정책 아이디
    private String category; // 카테고리
    private String title; // 정책명
    private String deadlineStatus; // 마감 상태
    private String hostDep; // 주관 기관명
    private boolean isScrap; // 스크랩 여부
    private long scrapCount; // 스크랩 수
    private String departmentImgUrl; // 중앙부처 이미지 url
    private String region; // 지역

    public static PolicyListResponseDto toListDto(Policy policy, Boolean isScrap, long scrapCount) {
        return PolicyListResponseDto.builder()
                .policyId(policy.getPolicyId())
                .category(policy.getCategory().getName())
                .title(policy.getTitle())
                .deadlineStatus(DeadlineStatusCalculator.calculateDeadline(policy.getApplyDue()))
                .hostDep(policy.getHostDep())
                .isScrap(isScrap)
                .scrapCount(scrapCount)
                .departmentImgUrl(policy.getDepartment().getImage_url() == null ? null : policy.getDepartment().getImage_url())
                .region(policy.getRegion().getName())
                .build();
    }
}
