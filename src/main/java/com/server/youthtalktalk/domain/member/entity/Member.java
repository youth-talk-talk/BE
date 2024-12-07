package com.server.youthtalktalk.domain.member.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.policy.entity.Region;
import com.server.youthtalktalk.domain.post.entity.Post;
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

    @Column(unique = true, nullable = false)
    private String username; // 소셜 id를 해싱 처리한 값

    private String nickname;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Likes> likes = new ArrayList<>();

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

    /* 연관관계 편의 메서드 */
    public void removeLike(Likes like) {
        likes.remove(like);
        like.setMember(null);
    }
}