package com.server.youthtalktalk.domain.post;

import com.server.youthtalktalk.domain.Image;
import com.server.youthtalktalk.domain.Member;
import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PostComment;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Post {

    @Id
    @GeneratedValue
    @JoinColumn(name =  "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long view;

    @OneToMany(mappedBy = "post")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostComment> postComments = new ArrayList<>();
}
