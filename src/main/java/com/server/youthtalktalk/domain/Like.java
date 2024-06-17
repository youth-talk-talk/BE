package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.comment.Comment;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Like {
    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
