package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 댓글 조회 + 오래된 순 정렬
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ORDER BY pc.createdAt ASC")
    List<PostComment> findPostCommentsByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);

    // 정책 댓글 조회 + 오래된 순 정렬
    @Query("SELECT pc FROM PolicyComment pc WHERE pc.policy.policyId = :policyId ORDER BY pc.createdAt ASC")
    List<PolicyComment> findPolicyCommentsByPolicyIdOrderByCreatedAtAsc(@Param("policyId") String policyId);

    // 회원이 작성한 댓글 조회 + 최신 순 정렬
    List<Comment> findCommentsByWriterOrderByCreatedAtDesc(Member writer);

}

