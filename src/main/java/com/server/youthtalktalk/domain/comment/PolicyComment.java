package com.server.youthtalktalk.domain.comment;

import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
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
        policy.getPolicyComments().add(this);
    }

}
