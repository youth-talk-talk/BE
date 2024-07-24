package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Likes;
import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndComment(Member member, Comment comment);
}
