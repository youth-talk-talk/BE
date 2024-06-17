package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.post.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Policy {

    @Id
    @Column(name = "policy_id", columnDefinition = "VARCHAR(255)")
    private String id;

    private String content;

    @OneToMany(mappedBy = "policy")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "policy")
    private List<PolicyComment> policyComments = new ArrayList<>();
}
