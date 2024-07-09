package com.server.youthtalktalk.domain.post;

import com.server.youthtalktalk.domain.BaseTimeEntity;
import com.server.youthtalktalk.domain.Image;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.comment.PostComment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder // 상속관계에서는 @SuperBuilder를 사용해야하는데 이 부분에서 문제가 생김
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "post_type")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @JoinColumn(name =  "post_id")
    private Long id;

    private String title;
    private String content;
    private Long view;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> postComments = new ArrayList<>();

    /* 연관관계 메서드 */
    public void setWriter(Member member) {
        this.writer = member;
        member.getPosts().add(this);
    }
}
