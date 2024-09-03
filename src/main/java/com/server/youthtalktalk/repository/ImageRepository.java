package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Announcement;
import com.server.youthtalktalk.domain.image.Image;
import com.server.youthtalktalk.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("select pi from PostImage pi where pi.post = :post")
    List<Image> findAllByPost(Post post);
    @Query("select ai from AnnouncementImage ai where ai.announcement = :announcement")
    List<Image> findAllByAnnouncement(Announcement announcement);

    void deleteAllByImgUrl(String imgUrl);
    void deleteAllByImgUrlIn(List<String> imgUrls);
}
