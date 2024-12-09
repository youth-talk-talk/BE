package com.server.youthtalktalk.domain.post.repostiory;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepositoryCustom {
    /** 모든 게시글 검색 */
    Page<Post> findAllPosts(Member member, Pageable pageable);
    /** 조회수별 게시글 검색 */
    Page<Post> findAllPostsByView(Member member, Pageable pageable);
    /** 모든 게시글 키워드 검색 */
    Page<Post> findAllPostsByKeyword(Member member, String keyword, Pageable pageable);
    /** 나의 모든 게시글 검색*/
    Page<Post> findAllPostsByWriter(Pageable pageable, Member writer);
    /** 카테고리별 리뷰 검색 */
    Page<Post> findAllReviewsByCategory(Member member, List<Category> categories, Pageable pageable);
    /** 카테고리별 리뷰 조회수순 검색 */
    Page<Post> findAllReviewsByCategoryAndView(Member member, List<Category> categories, Pageable pageable);
    /** 모든 리뷰 키워드 검색 */
    Page<Post> findAllReviewsByKeyword(Member member, String keyword, Pageable pageable);
    /** 스크랩한 게시글 검색*/
    Page<Post> findAllByScrap(Member member, Pageable pageable);
}
