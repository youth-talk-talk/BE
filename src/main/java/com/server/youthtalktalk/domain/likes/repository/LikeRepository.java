package com.server.youthtalktalk.domain.likes.repository;

import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndComment(Member member, Comment comment);
    List<Likes> findAllByMemberOrderByCreatedAtDesc(Member member);
}
