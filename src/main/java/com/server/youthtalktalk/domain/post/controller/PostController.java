package com.server.youthtalktalk.domain.post.controller;

import com.server.youthtalktalk.domain.image.service.ImageService;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.post.dto.*;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.post.service.PostReadService;
import com.server.youthtalktalk.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import static com.server.youthtalktalk.domain.post.dto.PostListRepDto.*;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostReadService postReadService;
    private final MemberService memberService;
    private final ImageService imageService;

    /** 게시글 생성 API */
    @PostMapping("")
    public BaseResponse<PostRepDto> create(@RequestBody @Valid PostCreateReqDto postCreateReqDto) throws IOException {
        PostRepDto postRepDto = postService.createPost(postCreateReqDto,memberService.getCurrentMember());
        return new BaseResponse<>(postRepDto, BaseResponseCode.SUCCESS);
    }

    /** 게시글 수정 API */
    @PatchMapping("/{id}")
    public BaseResponse<PostRepDto> update(@PathVariable Long id, @RequestBody @Valid PostUpdateReqDto postUpdateReqDto) throws IOException {
        PostRepDto postRepDto = postService.updatePost(id,postUpdateReqDto,memberService.getCurrentMember());
        return new BaseResponse<>(postRepDto, BaseResponseCode.SUCCESS);
    }

    /** 게시글 삭제 API */
    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Long id){
        postService.deletePost(id,memberService.getCurrentMember());
        return new BaseResponse<>(BaseResponseCode.SUCCESS);
    }

    /** 게시글 스크랩 API */
    @PostMapping("/{id}/scrap")
    public BaseResponse<String> scrap(@PathVariable Long id){
        if(postService.scrapPost(id,memberService.getCurrentMember()) != null)
            return new BaseResponse<>(BaseResponseCode.SUCCESS_SCRAP);
        else
            return new BaseResponse<>(BaseResponseCode.SUCCESS_SCRAP_CANCEL);
    }

    /** 이미지 업로드 API **/
    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        String imgUrl = imageService.uploadMultiFile(image);
        imageService.createPostImage(imgUrl);
        return new BaseResponse<>(imgUrl, BaseResponseCode.SUCCESS);
    }

    // READ
    /** 게시글 상세 조회 API */
    @GetMapping("/{id}")
    public BaseResponse<PostRepDto> detail(@PathVariable Long id){
        PostRepDto postRepDto = postReadService.getPostById(id,memberService.getCurrentMember());
        return new BaseResponse<>(postRepDto,BaseResponseCode.SUCCESS);
    }

    /** 자유 게시글 전체 조회 API */
    @GetMapping("/post")
    public BaseResponse<PostListRepDto> getAllPost(@PageableDefault(size = 10) Pageable pageable){
        PostListRepDto postListRepDto = postReadService.getAllPost(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(postListRepDto,BaseResponseCode.SUCCESS);
    }

    /** 리뷰 전체 조회 API */
    @GetMapping("/review")
    public BaseResponse<ReviewListRepDto> getAllReview(@PageableDefault(size = 10) Pageable pageable, @RequestParam(required = false) List<Category> categories){
        ReviewListRepDto reviewListRepDto = postReadService.getAllReviewByCategory(pageable,categories,memberService.getCurrentMember());
        return new BaseResponse<>(reviewListRepDto,BaseResponseCode.SUCCESS);
    }

    /** 게시글 키워드 검색 API */
    @GetMapping("/keyword")
    public BaseResponse<PostListResponse> getAllPostByKeyword(@PageableDefault(size = 10) Pageable pageable, @RequestParam String keyword, @RequestParam String type){
        PostListResponse postListResponse = postReadService.getAllPostByKeyword(pageable,type,keyword,memberService.getCurrentMember());
        return new BaseResponse<>(postListResponse,BaseResponseCode.SUCCESS);
    }

    /** 나의 모든 게시글 조회 API */
    @GetMapping("/me")
    public BaseResponse<List<PostListDto>> getAllMyPost(@PageableDefault(size = 10) Pageable pageable){
        List<PostListDto> postListDto = postReadService.getAllMyPost(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(postListDto,BaseResponseCode.SUCCESS);
    }

    /** 나의 스크랩한 게시글 조회 API */
    @GetMapping("/scrap")
    public BaseResponse<List<ScrapPostListDto>> getAllMyScrapedPost(@PageableDefault(size = 10) Pageable pageable){
        List<ScrapPostListDto> postListDto = postReadService.getScrapPostList(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(postListDto,BaseResponseCode.SUCCESS);
    }
}
