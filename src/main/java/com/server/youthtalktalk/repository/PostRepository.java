package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    /** 모든 게시글 검색 */
    @Query("SELECT p FROM Post p WHERE TYPE(p) = Post")
    Page<Post> findAllPosts(Pageable pageable);

    /** 조회수별 게시글 검색 */
    @Query("SELECT p FROM Post p WHERE TYPE(p) = Post order by p.view DESC ")
    Page<Post> findAllPostsByView(Pageable pageable);

    /** 모든 게시글 키워드 검색 */
    @Query("SELECT p FROM Post p WHERE TYPE(p) = Post AND REPLACE(p.title, ' ', '') LIKE %:keyword%")
    Page<Post> findAllPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 나의 모든 게시글 검색*/
    Page<Post> findAllPostsByWriter(Pageable pageable, Member writer);

    /** 카테고리별 리뷰 검색 */
    @Query("SELECT r FROM Review  r WHERE TYPE(r) = Review AND r.policy.category IN :categories")
    Page<Post> findAllReviewsByCategory(@Param("categories") List<Category> categories, Pageable pageable);

    /** 카테고리별 리뷰 조회수순 검색 */
    @Query("SELECT r FROM Review  r WHERE TYPE(r) = Review AND r.policy.category IN :categories ORDER BY r.view desc ")
    Page<Post> findAllReviewsByCategoryAndView(@Param("categories") List<Category> categories, Pageable pageable);

    /** 모든 리뷰 키워드 검색 */
    @Query("SELECT r FROM Review r WHERE TYPE(r) = Review AND REPLACE(r.title, ' ', '') LIKE %:keyword% OR REPLACE(r.policy.title,' ','') LIKE %:keyword%")
    Page<Post> findAllReviewsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 스크랩한 게시글 검색*/
    @Query("SELECT p FROM Post p JOIN Scrap s ON CONCAT(p.id,'') = s.itemId WHERE s.member = :member ORDER BY s.id DESC")
    Page<Post> findAllByScrap(@Param("member") Member member, Pageable pageable);
}
