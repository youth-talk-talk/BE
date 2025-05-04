package com.server.youthtalktalk.domain.member.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.image.entity.ProfileImage;
import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.report.entity.Report;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Block> blocks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recent_viewed_policies")
    private List<Long> recentViewedPolicies = new LinkedList<>();

    @JoinColumn(name = "img_id")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProfileImage profileImage;

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

    // block 추가
    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    // block 삭제
    public void removeBlock(Block block) {
        this.blocks.remove(block);
    }

    // profileImage 업데이트
    public void updateProfileImage(ProfileImage updateProfileImage) {
        this.profileImage = updateProfileImage;
    }

    // recentViewedPolicies 정책 추가
    public void addRecentViewedPolicies(Long policyId) {
        // 중복인 경우 순서 유지를 위해 기존 값 제거 후 추가
        if (recentViewedPolicies.contains(policyId)) {
            recentViewedPolicies.remove(policyId);
        }
        recentViewedPolicies.add(policyId);

        // 20개 초과 시 가장 오래된 정책 제거
        if (recentViewedPolicies.size() > 20) {
            recentViewedPolicies.remove(0); // 가장 오래된 값 제거
        }
    }

    /* 연관관계 편의 메서드 */
    public void removeLike(Likes like) {
        likes.remove(like);
        like.setMember(null);
    }
}