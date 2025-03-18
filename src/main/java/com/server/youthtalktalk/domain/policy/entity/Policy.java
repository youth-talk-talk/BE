package com.server.youthtalktalk.domain.policy.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.policy.entity.condition.*;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.post.entity.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Policy extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String policyId; // 정책 아이디

    @Enumerated(EnumType.STRING)
    private Region region; // 지역

    @Enumerated(EnumType.STRING)
    private InstitutionType institutionType; // 담당기관 구분(중앙부처, 지자체)

    private String title; // 정책명

    @Column(columnDefinition = "TEXT")
    private String introduction; // 정책 소개

    @Column(columnDefinition = "TEXT")
    private String supportDetail; // 지원 내용

    @Column(columnDefinition = "TEXT")
    private String applyTerm; // 신청기간

    @Enumerated(EnumType.STRING)
    private RepeatCode repeatCode; // 신청 기간 반복 코드

    private LocalDate applyStart; // 신청 시작일

    private LocalDate applyDue; // 신청 마감일

    private int minAge; // 최소 연령

    private int maxAge; // 최대 연령

    @Column(columnDefinition = "TEXT")
    private String addition; // 추가 사항

    @Column(columnDefinition = "TEXT")
    private String applLimit; // 참여 제한 대상

    @Column(columnDefinition = "TEXT")
    private String applStep; // 신청 절차

    @Column(columnDefinition = "TEXT")
    private String submitDoc; // 제출 서류

    @Column(columnDefinition = "TEXT")
    private String evaluation; // 평가 방법

    @Column(length = 500)
    private String applUrl; // 신청 사이트

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

    /** 신규 필드 */
    @NotNull
    private boolean isLimitedAge;

    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    @Enumerated(EnumType.STRING)
    private Earn earn; // 소득 제한 요건

    private int minEarn; // 최소 소득

    private int maxEarn; // 최대 소득

    @Column(columnDefinition = "TEXT")
    private String earnEtc; // 소득 기타 내용

    @Column(columnDefinition = "TEXT")
    private String zipCd;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Specialization> specialization; // 특화 분야 조건

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Major> major; // 전공 요건

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Education> education; // 학력 요건

    @Enumerated(EnumType.STRING)
    private Marriage marriage; // 결혼 요건

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Employment> employment; // 취업 요건

    private LocalDate bizStart; // 운영 시작일

    private LocalDate bizDue; // 운영 종료일

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<PolicyComment> policyComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicySubRegion> policySubRegions = new ArrayList<>();
}