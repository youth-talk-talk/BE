package com.server.youthtalktalk.domain.post.service;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.image.entity.PostImage;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.dto.*;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.domain.image.service.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PolicyRepository policyRepository;
    private final ImageService imageService;
    private final ScrapRepository scrapRepository;

    @Override
    public PostRepDto createPost(PostCreateReqDto postCreateReqDto, Member writer){
        Post post;
        if(postCreateReqDto.postType().equals("post")){ // 자유글
            post = Post.builder()
                    .title(postCreateReqDto.title())
                    .contents(postCreateReqDto.contentList())
                    .view(0L)
                    .build();
        }
        else if(postCreateReqDto.postType().equals("review")){ // 리뷰
            Review review = Review.builder()
                    .title(postCreateReqDto.title())
                    .contents(postCreateReqDto.contentList())
                    .view(0L)
                    .build();
            Policy policy = policyRepository.findById(postCreateReqDto.policyId()).orElseThrow(PolicyNotFoundException::new);
            review.setPolicy(policy);
            post = review;
        }
        else throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);

        post.setWriter(writer);
        Post savedPost = postRepository.save(post);

        // 이미지 리스트 추출 후 기존 PostImage 매핑
        imageService.mappingPostImage(extractImageUrl(post),post);
        log.info("게시글 생성 성공, postId = {}", savedPost.getId());
        return savedPost.toPostRepDto(scrapRepository.existsByMemberIdAndItemIdAndItemType(writer.getId(),post.getId().toString(),ItemType.POST));
    }

    @Override
    public PostRepDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, Member writer){
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter()==null || post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
            throw new BusinessException(BaseResponseCode.POST_ACCESS_DENIED);
        }

        Post updatedPost = post.toBuilder()
                .title(postUpdateReqDto.getTitle())
                .contents(postUpdateReqDto.getContentList())
                .build();
        if(post instanceof Review){ // 리뷰이면
            if(postUpdateReqDto.getPolicyId()!=null&&!postUpdateReqDto.getPolicyId().isEmpty()){
                Policy policy = policyRepository.findById(postUpdateReqDto.getPolicyId()).orElseThrow(PolicyNotFoundException::new);
                ((Review)updatedPost).setPolicy(policy);
            }
        }
        Post savedPost = postRepository.save(updatedPost);
        // 추가된 이미지 매핑
        if(postUpdateReqDto.getAddImgUrlList()!=null&&!postUpdateReqDto.getAddImgUrlList().isEmpty()){
            imageService.mappingPostImage(postUpdateReqDto.getAddImgUrlList(),savedPost);
        }
        if(postUpdateReqDto.getDeletedImgUrlList()!=null&&!postUpdateReqDto.getDeletedImgUrlList().isEmpty()){
            imageService.deleteMultiFile(postUpdateReqDto.getDeletedImgUrlList());
        }
        log.info("게시글 수정 성공, postId = {}", savedPost.getId());
        return savedPost.toPostRepDto(scrapRepository.existsByMemberIdAndItemIdAndItemType(writer.getId(),post.getId().toString(),ItemType.POST));
    }

    @Override
    public void deletePost(Long postId, Member writer) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter() == null || post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
            throw new BusinessException(BaseResponseCode.POST_ACCESS_DENIED);
        }
        // 1. 게시글 이미지 삭제
        List<String> imageUrls = post.getImages().stream()
                .map(PostImage::getImgUrl)
                .toList();
        imageService.deleteMultiFile(imageUrls);
        // 2. 게시글 스크랩 삭제
        scrapRepository.deleteAllByItemIdAndItemType(post.getId().toString(), ItemType.POST);
        // 3. 게시글 삭제
        postRepository.delete(post);
        log.info("게시글 삭제 성공, postId = {}", postId);
    }

    @Override
    public Scrap scrapPost(Long postId, Member member) {
        postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Scrap scrap = scrapRepository.findByMemberAndItemIdAndItemType(member,postId.toString(), ItemType.POST).orElse(null);
        if(scrap!=null){
            scrapRepository.delete(scrap);
            return null;
        }
        else{
            return scrapRepository.save(Scrap.builder() // 스크랩할 경우
                    .itemId(postId.toString())
                    .itemType(ItemType.POST)
                    .member(member)
                    .build());
        }
    }

    @Override
    public List<String> extractImageUrl(Post post){
        List<String> imgUrls = new ArrayList<>();
        for(Content content : post.getContents()){
            if(content.getType().equals(ContentType.IMAGE)){
                imgUrls.add(content.getContent());
            }
        }
        return imgUrls;
    }
}
