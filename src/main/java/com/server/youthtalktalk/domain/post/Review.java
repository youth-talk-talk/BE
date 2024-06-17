package com.server.youthtalktalk.domain.post;

import com.server.youthtalktalk.domain.policy.Policy;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("review")
public class Review extends Post {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;
}
