package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    List<Comment> findByPolicyIdOrderByCreatedAtAsc(String policyId);
}
