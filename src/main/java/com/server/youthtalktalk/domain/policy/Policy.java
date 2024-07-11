package com.server.youthtalktalk.domain.policy;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.post.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Policy extends BaseTimeEntity {
    @Id
    private String policyId; // 정책 아이디

    @Enumerated(EnumType.STRING)
    private Region region; // 지역

    private String title; // 정책명

    @Column(columnDefinition = "TEXT")
    private String introduction; // 정책 소개

    @Column(columnDefinition = "TEXT")
    private String supportDetail; // 지원 내용

    @Enumerated(EnumType.STRING)
    private RepeatCode repeatCode; // 반복 코드

    @Column(columnDefinition = "TEXT")
    private String applyTerm; // 신청기간

    @Column(columnDefinition = "TEXT")
    private String operationTerm; // 운영기간

    private LocalDate applyDue; // 신청 마감일

    private int minAge; // 최소 연령

    private int maxAge; // 최대 연령

    @Column(columnDefinition = "TEXT")
    private String employment; // 취업 상태

    private String employmentCode; // 취업 상태 코드 리스트

    private String specialization; // 특화 분야

    private String major; // 전공 요건

    private String education; // 학력 요건

    @Column(columnDefinition = "TEXT")
    private String addrIncome; // 거주지 및 소득 조건

    @Column(columnDefinition = "TEXT")
    private String addition; // 추가 사항

    @Column(columnDefinition = "TEXT")
    private String applLimit; // 참여 제한 대상

    @Column(columnDefinition = "TEXT")
    private String applStep; // 신청 절차

    @Column(columnDefinition = "TEXT")
    private String submitDoc; // 제출 서류

    @Column(columnDefinition = "TEXT")
    private String evaluation; // 심사 발표

    @Column(length = 500)
    private String applUrl; // 신청 사이트

    @Column(length = 500)
    private String formattedApplUrl; // 신청 사이트 (전처리)

    @Column(length = 500)
    private String refUrl1; // 참고 사이트 1

    @Column(length = 500)
    private String refUrl2; // 참고 사이트 2

    private String hostDep; // 주관 부처명

    private String operatingOrg; // 운영 기관명

    @Column(columnDefinition = "TEXT")
    private String etc; // 기타 사항

    private long view; // 조회수

    @Enumerated(EnumType.STRING)
    private Category category; // 카테고리

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<PolicyComment> policyComments = new ArrayList<>();
}