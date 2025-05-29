package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Getter
@Builder
public class PolicyDetailResponseDto {
    private String departmentImgUrl; // 중앙 부처 이미지
    private Boolean isScrap;// 스크랩 여부
    private String recruitmentType; // 모집 상태
    private String title; // 정책명
    private String region; // 지역
    private String category; // 정책 분야
    private String hostDep; // 주관 부처명 (주관 기관)
    private String applyTerm; // 신청 기간
    private String introduction; // 정책 요약
    private String age; // 연령
    private String supportDetail; // 지원 내용
    private String addition; // 추가 사항
    private String applLimit; // 참여 제한 대상
    private String applStep; // 신청 절차
    private String submitDoc; // 제출 서류
    private String evaluation; // 평가 방법
    private String applUrl; // 신청 사이트
    private String refUrl1; // 참고 사이트 1
    private String refUrl2; // 참고 사이트 2
    private String etc; // 기타 사항
    private String subRegion; // 세부 지역
    private String earnEtc; // 소득 요건
    private String specialization; // 특화 분야
    private String major; // 전공 요건
    private String education; // 학력 요건
    private String marriage; // 결혼 요건
    private String employment; // 취업 요건

    public static PolicyDetailResponseDto toDto(Policy policy, Boolean isScrap) {
        // 신청 기간 날짜 포맷팅
        String applyTerm = formatApplyTerm(policy.getApplyStart(), policy.getApplyDue());

        // 모집 상태 ( 상시 모집 / 모집 마감 / D-000 / 마감 임박 으로 구분 ) - 공고의 마감일이 D-7 이하로 남을 경우에 마감 임박
        String recruitmentType = getRecruitmentType(policy.getApplyDue());

        return PolicyDetailResponseDto.builder()
                .departmentImgUrl(policy.getDepartment().getImage_url()) // 중앙부처 이미지 url
                .isScrap(isScrap)// 스크랩 여부
                .recruitmentType(recruitmentType) // 모집 상태
                .title(policy.getTitle()) // 정책명
                .region(policy.getRegion().getName()) // 지역
                .category(policy.getCategory().getName()) // 정책 분야
                .hostDep(policy.getHostDep()) // 주관 부처명 (주관 기관)
                .applyTerm(applyTerm) // 신청 기간
                .introduction(policy.getIntroduction()) // 정책 요약
                .age("만 "+ policy.getMinAge() + "세 ~ 만 " + policy.getMaxAge() + "세") // 연령
                .supportDetail(policy.getSupportDetail()) // 지원 내용
                .addition(sanitize(policy.getAddition())) // 추가 사항
                .applLimit(sanitize(policy.getApplLimit())) // 참여 제한 대상
                .applStep(sanitize(policy.getApplStep())) // 신청 절차
                .submitDoc(sanitize(policy.getSubmitDoc())) // 제출 서류
                .evaluation(sanitize(policy.getEvaluation())) // 평가 방법
                .applUrl(sanitize(policy.getApplUrl())) // 신청 사이트
                .refUrl1(sanitize(policy.getRefUrl1())) // 참고 사이트 1
                .refUrl2(sanitize(policy.getRefUrl2())) // 참고 사이트 2
                .etc(sanitize(policy.getEtc())) // 기타 사항
                .subRegion(sanitize(
                        policy.getPolicySubRegions().stream()
                                .map(psr -> psr.getSubRegion().getName())
                                .collect(Collectors.joining(", "))
                )) // 세부 지역 (, 로 연결)
                .earnEtc(sanitize(policy.getEarnEtc())) // 소득 요건
                .specialization(sanitize(
                        policy.getSpecialization().isEmpty() ? null :
                                policy.getSpecialization().stream()
                                        .map(Specialization::getName)
                                        .collect(Collectors.joining(", "))
                )) // 특화 분야
                .major(sanitize(
                        policy.getMajor().isEmpty() ? null :
                                policy.getMajor().stream()
                                        .map(Major::getName)
                                        .collect(Collectors.joining(", "))
                )) // 전공 요건
                .education(sanitize(
                        policy.getEducation().isEmpty() ? null :
                                policy.getEducation().stream()
                                        .map(Education::getName)
                                        .collect(Collectors.joining(", "))
                )) // 학력 요건
                .marriage(sanitize(policy.getMarriage().getName())) // 결혼 요건
                .employment(sanitize(
                        policy.getEmployment().isEmpty() ? null :
                                policy.getEmployment().stream()
                                        .map(Employment::getName)
                                        .collect(Collectors.joining(", "))
                )) // 취업 요건
                .build();
    }

    private static String sanitize(String value) {
        if (value == null) return null;
        if (value.trim().equals("-") || value.trim().equals("제한없음") || value.trim().equals("없음") || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    private static String getRecruitmentType(LocalDate dueDate) {
        if (dueDate == null) return "상시 모집";

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        if (daysLeft < 0) return "모집 마감";
        if (daysLeft <= 7) return "마감 임박";
        return "D-" + daysLeft;
    }

    private static String formatApplyTerm(LocalDate start, LocalDate end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        if (start == null || end == null) {
            return "상시";
        }
        return start.format(formatter) + " ~ " + end.format(formatter);
    }

}