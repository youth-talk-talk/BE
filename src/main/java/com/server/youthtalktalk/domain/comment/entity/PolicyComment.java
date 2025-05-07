package com.server.youthtalktalk.domain.comment.entity;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("policy")
public class PolicyComment extends Comment{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    /* 연관관계 메서드 */
    public void setPolicy(Policy policy) {
        this.policy = policy;
        if (policy != null)
            policy.getPolicyComments().add(this);
    }

    @Override
    public Long getArticleId() {
        return policy.getPolicyId();
    }

    @Override
    public String getArticleType() {
        return "policy";
    }

    @Override
    public String getArticleTitle() {
        return policy.getTitle();
    }
}
