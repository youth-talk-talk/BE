package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String nickname;

    private String email;
    private String profileUrl;
    private UserType userType;
    private String region;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Likes> likes = new ArrayList<>();

}
