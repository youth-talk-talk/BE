package com.server.youthtalktalk.domain.policy.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.policy.entity.condition.*;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.post.entity.Review;
import jakarta.persistence.*;
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
    @Column(name = "policy_id", updatable = false)
    private Long policyId;

    @Column(name = "policy_num", length = 30, nullable = false, unique = true)
    private String policyNum; // 정책 번호

    @Enumerated(EnumType.STRING)
    @Column(name = "region", length = 20)
    private Region region; // 지역

    @Enumerated(EnumType.STRING)
    @Column(name = "institutionType", length = 20)
    private InstitutionType institutionType; // 담당기관 구분(중앙부처, 지자체)

    @Column(name = "title", length = 150)
    private String title; // 정책명

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction; // 정책 소개

    @Column(name = "supportDetail", columnDefinition = "TEXT")
    private String supportDetail; // 지원 내용

    @Column(name = "applyTerm", columnDefinition = "TEXT")
    private String applyTerm; // 신청기간

    @Enumerated(EnumType.STRING)
    @Column(name = "repeatCode", length = 20)
    private RepeatCode repeatCode; // 신청 기간 반복 코드

    @Column(name = "applyStart")
    private LocalDate applyStart; // 신청 시작일

    @Column(name = "applyDue")
    private LocalDate applyDue; // 신청 마감일

    @Column(name = "minAge")
    private Integer minAge; // 최소 연령

    @Column(name = "maxAge")
    private Integer maxAge; // 최대 연령

    @Column(name = "addition", columnDefinition = "TEXT")
    private String addition; // 추가 사항

    @Column(name = "applLimit", columnDefinition = "TEXT")
    private String applLimit; // 참여 제한 대상

    @Column(name = "applStep", columnDefinition = "TEXT")
    private String applStep; // 신청 절차

    @Column(name = "submitDoc", columnDefinition = "TEXT")
    private String submitDoc; // 제출 서류

    @Column(name = "evaluation", columnDefinition = "TEXT")
    private String evaluation; // 평가 방법

    @Column(name = "applUrl", length = 255)
    private String applUrl; // 신청 사이트

    @Column(name = "refUrl1", length = 255)
    private String refUrl1; // 참고 사이트 1

    @Column(name = "refUrl2", length = 255)
    private String refUrl2; // 참고 사이트 2

    @Column(name = "hostDep", length = 255)
    private String hostDep; // 주관 부처명

    @Column(name = "operatingOrg", length = 255)
    private String operatingOrg; // 운영 기관명

    @Column(name = "etc", columnDefinition = "TEXT")
    private String etc; // 기타 사항

    @Column(name = "view")
    private Long view; // 조회수

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private Category category; // 카테고리

    /** 신규 필드 */
    @Column(name = "isLimitedAge")
    private Boolean isLimitedAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "subCategory", length = 50)
    private SubCategory subCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "earn", length = 20)
    private Earn earn; // 소득 제한 요건

    @Column(name = "minEarn")
    private Integer minEarn; // 최소 소득

    @Column(name = "maxEarn")
    private Integer maxEarn; // 최대 소득

    @Column(name = "earnEtc", columnDefinition = "TEXT")
    private String earnEtc; // 소득 기타 내용

    @Column(name = "zipCd", columnDefinition = "TEXT")
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
    @Column(name = "marriage", length = 255)
    private Marriage marriage; // 결혼 요건

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Employment> employment; // 취업 요건

    @Column(name = "bizStart")
    private LocalDate bizStart; // 운영 시작일

    @Column(name = "bizDue")
    private LocalDate bizDue; // 운영 종료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(length = 10)
    private String hostDepCode;

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<Review> reviews = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "policy")
    private List<PolicyComment> policyComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicySubRegion> policySubRegions = new ArrayList<>();

    /** 연관관계 메서드 */
    void setDepartment(Department department) {
        this.department = department;
        if(department != null) {
            department.getPolicies().add(this);
        }
    }
}