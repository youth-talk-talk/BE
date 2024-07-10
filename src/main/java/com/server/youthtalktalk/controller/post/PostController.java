package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.dto.post.PostUpdateReqDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.member.MemberService;
import com.server.youthtalktalk.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MemberService memberService;

    /** 게시글 생성 API */
    @PostMapping()
    public BaseResponse<PostRepDto> create(@RequestPart("content") @Valid PostCreateReqDto postCreateReqDto,
                                           @RequestPart("images") List<MultipartFile> images) throws IOException {
        PostRepDto repDto = postService.createPost(postCreateReqDto,images,memberService.getCurrentMember());
        return new BaseResponse<>(repDto, BaseResponseCode.SUCCESS);
    }

    /** 게시글 수정 API */
    @PatchMapping("/{postId}")
    public BaseResponse<PostRepDto> update(@RequestPart("content") @Valid PostUpdateReqDto postUpdateReqDto,
                                           @RequestPart("images") List<MultipartFile> images,
                                           @PathVariable Long postId) throws IOException {
        PostRepDto repDto = postService.updatePost(postId,postUpdateReqDto,images,memberService.getCurrentMember());
        return new BaseResponse<>(repDto, BaseResponseCode.SUCCESS);
    }

    /** 게시글 삭제 API */
    @DeleteMapping("/{postId}")
    public BaseResponse<String> delete(@PathVariable Long postId){
        postService.deletePost(postId,memberService.getCurrentMember());
        return new BaseResponse<>(BaseResponseCode.SUCCESS);
    }

}
