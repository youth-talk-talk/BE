package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt ASC")
    List<Comment> findPostCommentsByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);

    @Query("SELECT pc FROM PolicyComment pc WHERE pc.policy.policyId = :policyId ORDER BY pc.createdAt ASC")
    List<Comment> findPolicyCommentsByPolicyIdOrderByCreatedAtAsc(@Param("policyId") String policyId);

}

