package com.server.youthtalktalk.domain.comment;

import com.server.youthtalktalk.domain.Likes;
import com.server.youthtalktalk.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    private String content;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "comment")
    private List<Likes> commentLikes = new ArrayList<>();

    /* 연관관계 메서드 */
    public void setWriter(Member member) {
        this.writer = member;
        if (member != null) {
            member.getComments().add(this);
        }
    }
}
