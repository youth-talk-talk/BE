package com.server.youthtalktalk.domain;

import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Image {

    @Id
    @GeneratedValue
    @Column(name = "img_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /* 연관관계 메서드 */
    public void setPost(Post post) {
        this.post = post;
        post.getImages().add(this);
    }
}
