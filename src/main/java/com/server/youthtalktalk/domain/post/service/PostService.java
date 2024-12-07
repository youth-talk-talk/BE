package com.server.youthtalktalk.domain.post.service;

import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.post.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostRepDto createPost(PostCreateReqDto postCreateReqDto, List<MultipartFile> fileList, Member writer) throws IOException;
    PostRepDto createPostTest(PostCreateTestReqDto postCreateTestReqDto, Member writer) throws IOException;
    PostRepDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, List<MultipartFile> fileList, Member writer) throws IOException;
    PostRepDto updatePostTest(Long postId, PostUpdateReqTestDto postUpdateReqDto, Member writer) throws  IOException;
    void deletePost(Long postId, Member writer);
    Scrap scrapPost(Long postId, Member member);
}
