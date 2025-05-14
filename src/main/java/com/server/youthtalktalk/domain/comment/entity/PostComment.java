package com.server.youthtalktalk.domain.comment.entity;

import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Override
    public Long getArticleId() {
        return post.getId();
    }

    @Override
    public String getArticleType() {
        return ((Hibernate.getClass(post).equals(Review.class))) ? "review" : "post";
    }

    @Override
    public String getArticleTitle() {
        return post.getTitle();
    }
}
