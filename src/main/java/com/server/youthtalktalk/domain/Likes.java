package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /* 연관관계 메서드 */
    public void setMember(Member member) {
        this.member = member;
        if (member != null && !member.getLikes().contains(this)) {
            member.getLikes().add(this);
        }
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        if (comment != null && !comment.getCommentLikes().contains(this)) {
            comment.getCommentLikes().add(this);
        }
    }

}
