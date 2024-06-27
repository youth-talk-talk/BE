package com.server.youthtalktalk.domain.member;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.Likes;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickname;
    private String socialId;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Likes> likes = new ArrayList<>();

    // 회원 권한 부여
    public void authorizeMember(Role role) {
        this.role = Role.MEMBER;
    }

    // refresh token 업데이트
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    // refresh token 제거
    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

}
