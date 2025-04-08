package com.server.youthtalktalk.domain.policy.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchConditionRequestDto {
    private String keyword; // 검색 키워드
    private String institutionType; // 담당기관 (중앙부처 또는 지자체)
    private List<String> category; // 카테고리
    private String marriage; // 결혼요건
    private String age; // 연령
    private String minEarn; // 최소 소득
    private String maxEarn; // 최대 소득
    private List<String> education; // 학력
    private List<String> employment; // 취업상태
    private List<String> major; // 전공요건
    private List<String> specialization; // 특화분야
    private List<String> region; // 지역 (소분류 포함)
    private Boolean isFinished; // 마감여부
    private String applyDue; // 마감일
}
