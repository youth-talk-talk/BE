package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Image;
import com.server.youthtalktalk.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByPost(Post post);
    void deleteAllByImgUrl(String imgUrl);
}
