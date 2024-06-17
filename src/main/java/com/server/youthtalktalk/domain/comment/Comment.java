package com.server.youthtalktalk.domain.comment;

import com.server.youthtalktalk.domain.Like;
import com.server.youthtalktalk.domain.Member;
import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Comment {

    @Id
    @Generated
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long view;

    @OneToMany(mappedBy = "comment")
    private List<Like> commentLikes = new ArrayList<>();
}
