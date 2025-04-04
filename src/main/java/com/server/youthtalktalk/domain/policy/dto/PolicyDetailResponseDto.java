package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PolicyDetailResponseDto {
    private String title; // 정책명
    private String introduction; // 정책 소개
    private String supportDetail; // 지원 내용
    private String applyTerm; // 신청기간
    private String operationTerm; // 운영기간
    private String age; // 연령
    private String addrIncome; // 거주지 및 소득 조건
    private String education; // 학력 요건
    private String major; // 전공 요건
    private String employment; // 취업 상태
    private String specialization; // 특화 분야
    private String applLimit; // 참여 제한 대상
    private String addition; // 추가 사항
    private String applStep; // 신청 절차
    private String evaluation; // 심사 발표
    private String applUrl; // 신청 사이트
    private String submitDoc; // 제출 서류
    private String etc; // 기타 사항
    private String hostDep; // 주관 부처명
    private String operatingOrg; // 운영 기관명
    private String refUrl1; // 참고 사이트 1
    private String refUrl2; // 참고 사이트 2
    private String formattedApplUrl; // 신청 사이트 (전처리)
    private Boolean isScrap;// 스크랩 여부
    private String departmentImgUrl; // 중앙 부처 이미지
//    private Region region; // 지역
//    private Category category; // 카테고리
//    private LocalDate applyDue; // 신청 마감일
//    private String employmentCode; // 취업 상태 코드 리스트

    public static PolicyDetailResponseDto toDto(Policy policy, Boolean isScrap) {
        return PolicyDetailResponseDto.builder()
                .title(policy.getTitle()) // 정책명
                .introduction(policy.getIntroduction()) // 정책 소개
                .supportDetail(policy.getSupportDetail()) // 지원 내용
                .applyTerm(policy.getApplyTerm()) // 신청기간
                .operationTerm(null) // 운영기간
                .age("만 "+ policy.getMinAge() + "세 ~ 만 " + policy.getMaxAge() + "세") // 연령
                .addrIncome(null) // 거주지 및 소득 조건
                .education(null) // 학력 요건
                .major(null) // 전공 요건
                .employment(null) // 취업 상태
                .specialization(null) // 특화 분야
                .applLimit(policy.getApplLimit()) // 참여 제한 대상
                .addition(policy.getAddition()) // 추가 사항
                .applStep(policy.getApplStep()) // 신청 절차
                .evaluation(policy.getEvaluation()) // 심사 발표
                .applUrl(policy.getApplUrl()) // 신청 사이트
                .submitDoc(policy.getSubmitDoc()) // 제출 서류
                .etc(policy.getEtc()) // 기타 사항
                .hostDep(policy.getHostDep()) // 주관 부처명
                .operatingOrg(policy.getOperatingOrg()) // 운영 기관명
                .refUrl1(policy.getRefUrl1()) // 참고 사이트 1
                .refUrl2(policy.getRefUrl2()) // 참고 사이트 2
                .formattedApplUrl(null) // 신청 사이트 (전처리)
                .isScrap(isScrap)// 스크랩 여부
                .departmentImgUrl(policy.getDepartment().getImage_url()) // 중앙부처 이미지 url
                .build();
    }
}