package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

//    @PostMapping()
//    public BaseResponse<PostRepDto> create(@RequestPart("content")PostCreateReqDto postCreateReqDto,
//                                           @RequestPart("images") List<MultipartFile> images,
//                                           @AuthenticationPrincipal UserDetails userDetails){
//        PostRepDto repDto = postService.createPost(postCreateReqDto,images,userDetails.get)
//    }
}
