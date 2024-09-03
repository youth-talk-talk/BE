package com.server.youthtalktalk.domain.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.youthtalktalk.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("post")
public class PostImage extends Image{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    public void setPost(Post post){
        this.post = post;
        if(post != null){
            post.getImages().add(this);
        }
    }
}
