package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.dto.post.PostListRepDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.server.youthtalktalk.dto.post.PostListRepDto.*;

public interface PostReadService {
    PostRepDto getPostById(Long postId,Member member);
    PostListRepDto getAllPost(Pageable pageable, Member member);
    PostListRepDto getAllReviewByCategory(Pageable pageable, List<Category> category, Member member);
    List<PostListDto> getAllMyPost(Pageable pageable, Member member);
    PostListResponse getAllPostByKeyword(Pageable pageable, String type, String keyword, Member member);
    List<ScrapPostListDto> getScrapPostList(Pageable pageable, Member member);
}
