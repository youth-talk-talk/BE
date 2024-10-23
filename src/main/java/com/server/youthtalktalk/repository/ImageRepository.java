package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Announcement;
import com.server.youthtalktalk.domain.image.Image;
import com.server.youthtalktalk.domain.image.PostImage;
import com.server.youthtalktalk.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("select pi from PostImage pi where pi.post = :post")
    List<Image> findAllByPost(Post post);

    @Query("select ai from AnnouncementImage ai where ai.announcement = :announcement")
    List<Image> findAllByAnnouncement(Announcement announcement);

    List<Image> findAllByImgUrlIn(List<String> imgUrl);

    @Modifying
    @Query("DELETE FROM Image e WHERE e.imgUrl IN :imgUrls")
    void deleteAllByImgUrlIn(@Param("imgUrls") List<String> imgUrls);

    @Query("SELECT pi.imgUrl FROM PostImage pi WHERE pi.post IS NULL AND pi.createdAt <= :date")
    List<String> findAllByPostIsNull(LocalDateTime date);
}
