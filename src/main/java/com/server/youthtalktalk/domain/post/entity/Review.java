package com.server.youthtalktalk.domain.post.entity;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@DiscriminatorValue("review")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Review extends Post {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    /* 연관관계 메서드 */
    public void setPolicy(Policy policy) {
        this.policy = policy;
        policy.getReviews().add(this);
    }
}

