package com.server.youthtalktalk.controller.post;

import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.dto.post.*;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.member.MemberService;
import com.server.youthtalktalk.service.post.PostReadService;
import com.server.youthtalktalk.service.post.PostService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.server.youthtalktalk.dto.post.PostListRepDto.*;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostReadService postReadService;
    private final MemberService memberService;

    /** 게시글 생성 API */
    @PostMapping()
    public BaseResponse<PostRepDto> create(@RequestPart("content") @Valid PostCreateReqDto postCreateReqDto,
                                           @RequestPart(value = "images",required = false) List<MultipartFile> images) throws IOException {
        PostRepDto repDto = postService.createPost(postCreateReqDto,images,memberService.getCurrentMember());
        return new BaseResponse<>(repDto, BaseResponseCode.SUCCESS);
    }

    /** 게시글 수정 API */
    @PatchMapping("/{id}")
    public BaseResponse<PostRepDto> update(@RequestPart("content") @Valid PostUpdateReqDto postUpdateReqDto,
                                           @RequestPart(value = "images",required = false) List<MultipartFile> images,
                                           @PathVariable Long id) throws IOException {
        PostRepDto repDto = postService.updatePost(id,postUpdateReqDto,images,memberService.getCurrentMember());
        return new BaseResponse<>(repDto, BaseResponseCode.SUCCESS);
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
    @PostMapping("/review")
    public BaseResponse<PostListRepDto> getAllReview(@PageableDefault(size = 10) Pageable pageable, @RequestBody ReviewSearchReqDto reviewSearchReqDto){
        PostListRepDto postListRepDto = postReadService.getAllReviewByCategory(pageable,reviewSearchReqDto.getCategories(),memberService.getCurrentMember());
        return new BaseResponse<>(postListRepDto,BaseResponseCode.SUCCESS);
    }

    /** 게시글 키워드 검색 API */
    @GetMapping("/keyword")
    public BaseResponse<List<PostListDto>> getAllPostByKeyword(@PageableDefault(size = 10) Pageable pageable, @RequestParam String keyword, @RequestParam String type){
        List<PostListDto> postListDto = postReadService.getAllPostByKeyword(pageable,type,keyword,memberService.getCurrentMember());
        return new BaseResponse<>(postListDto,BaseResponseCode.SUCCESS);
    }

    /** 나의 모든 게시글 조회 API */
    @GetMapping("/me")
    public BaseResponse<List<PostListDto>> getAllMyPost(@PageableDefault(size = 10) Pageable pageable){
        List<PostListDto> postListDto = postReadService.getAllMyPost(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(postListDto,BaseResponseCode.SUCCESS);
    }

    /** 나의 스크랩한 게시글 조회 API */
    @GetMapping("/scrap")
    public BaseResponse<List<PostListDto>> getAllMyScrapedPost(@PageableDefault(size = 10) Pageable pageable){
        List<PostListDto> postListDto = postReadService.getScrapPostList(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(postListDto,BaseResponseCode.SUCCESS);
    }
}
