package com.server.youthtalktalk.domain.comment.entity;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "comment")
    private List<Likes> commentLikes = new ArrayList<>();

    /* 연관관계 메서드 */
    public void setWriter(Member member) {
        this.writer = member;
        if (member != null) {
            member.getComments().add(this);
        }
    }

    public void removeLike(Likes like) {
        commentLikes.remove(like);
        like.setComment(null);
    }

    // content 업데이트
    public void updateContent(String content) {
        this.content = content;
    }

    // 연관엔티티(post/policy) id 조회
    public abstract Object getRelatedEntityId();
}
