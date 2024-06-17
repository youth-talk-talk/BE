package com.server.youthtalktalk.domain.comment;

import com.server.youthtalktalk.domain.Policy;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("policy")
public class PolicyComment extends Comment{

    private boolean isFixed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

}
