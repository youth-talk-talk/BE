package com.server.youthtalktalk.domain.member;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.Likes;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Column(unique = true, nullable = false)
    private String username;

    private String email;
    private String nickname;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<Scrap> scraps = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<Likes> likes = new ArrayList<>();

    // 회원 권한 부여
    public void authorizeMember() {
        this.role = Role.USER;
    }

    // refresh token 업데이트
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    // refresh token 제거
    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    // nickname 업데이트
    public void updateNickname(String updateNickname) {
        this.nickname = updateNickname;
    }

    // region 업데이트
    public void updateRegion(Region updateRegion) {
        this.region = updateRegion;
    }
}