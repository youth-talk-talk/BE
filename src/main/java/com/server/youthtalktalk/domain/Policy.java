package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.comment.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
public class Policy {

    @Id
    @Column(name = "policy_id")
    private Long id;

    @OneToMany(mappedBy = "policy")
    private List<Comment> policyComments = new ArrayList<>();
}
