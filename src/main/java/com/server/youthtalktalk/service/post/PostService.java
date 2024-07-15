package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.dto.post.PostUpdateReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostRepDto createPost(PostCreateReqDto postCreateReqDto, List<MultipartFile> fileList, Member writer) throws IOException;
    PostRepDto updatePost(Long postId,PostUpdateReqDto postUpdateReqDto, List<MultipartFile> fileList, Member writer) throws IOException;
    void deletePost(Long postId, Member writer);
    Scrap scrapPost(Long postId, Member member);
}
