package com.server.youthtalktalk.domain.post.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.post.dto.PostListRepDto;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import com.server.youthtalktalk.domain.post.dto.ReviewListRepDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.server.youthtalktalk.domain.post.dto.PostListRepDto.*;

public interface PostReadService {
    PostRepDto getPostById(Long postId,Member member);
    PostListRepDto getAllPost(Pageable pageable, Member member);
    ReviewListRepDto getAllReviewByCategory(Pageable pageable, List<Category> category, Member member);
    PostListResponse getAllMyPost(Pageable pageable, Member member);
    PostListResponse getAllPostByKeyword(Pageable pageable, String type, String keyword, Member member);
    PostListResponse getScrapPostList(Pageable pageable, Member member);
    List<PostListDto> getTopPostsByView(Member member);
}
