package com.server.youthtalktalk.domain.post;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("review")
public class Review extends Post {
    private Long policyId;
}
