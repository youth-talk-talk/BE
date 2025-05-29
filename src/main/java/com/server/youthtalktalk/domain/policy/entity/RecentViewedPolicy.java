package com.server.youthtalktalk.domain.policy.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class RecentViewedPolicy extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_viewed_policy_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @Builder
    public RecentViewedPolicy(Long id, Member member, Policy policy) {
        this.id = id;
        this.member = member;
        this.policy = policy;
    }
}
