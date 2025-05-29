package com.server.youthtalktalk.domain.comment.repository;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글의 전체 댓글 조회
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId ")
    List<PostComment> findPostCommentsByPostId(@Param("postId") Long postId);

    // 특정 정책의 전체 댓글 조회
    @Query("SELECT pc FROM PolicyComment pc WHERE pc.policy.policyId = :policyId")
    List<PolicyComment> findPolicyCommentsByPolicyId(@Param("policyId") Long policyId);

    // 회원이 작성한 댓글 조회 + 최신 순 정렬
    List<Comment> findCommentsByWriterOrderByCreatedAtDesc(Member writer);

}

