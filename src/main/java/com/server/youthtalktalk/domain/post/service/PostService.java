package com.server.youthtalktalk.domain.post.service;

import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.dto.*;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostRepDto createPost(PostCreateReqDto postCreateReqDto, Member writer) throws IOException;
    PostRepDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, Member writer) throws  IOException;
    void deletePost(Long postId, Member writer);
    Scrap scrapPost(Long postId, Member member);
    List<String> extractImageUrl(Post post);
}
