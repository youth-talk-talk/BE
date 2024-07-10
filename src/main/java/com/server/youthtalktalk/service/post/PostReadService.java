package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.post.PostListRepDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostReadService {
    PostRepDto getPostById(Long postId);
    PostListRepDto getAllPost(Pageable pageable);
    PostListRepDto getAllReviewByCategory(Pageable pageable, List<String> category);
    PostListRepDto getAllMyPost(Pageable pageable, String type, Member member);
    PostListRepDto getAllPostByKeyword(Pageable pageable, String keyword, String type);
}
