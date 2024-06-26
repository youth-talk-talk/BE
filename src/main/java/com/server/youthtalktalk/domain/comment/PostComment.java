package com.server.youthtalktalk.domain.comment;

import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("post")
public class PostComment extends Comment{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /* 연관관계 메서드 */
    public void setPost(Post post) {
        this.post = post;
        post.getPostComments().add(this);
    }

}
