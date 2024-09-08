package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.image.PostImage;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Content;
import com.server.youthtalktalk.domain.post.ContentType;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.*;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import com.server.youthtalktalk.service.image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public PostRepDto createPost(PostCreateReqDto postCreateReqDto, List<MultipartFile> fileList, Member writer) throws IOException {
        Post post;
        if(postCreateReqDto.getPostType().equals("post")){ // 자유글
            post = Post.builder()
                    .title(postCreateReqDto.getTitle())
                    .content(postCreateReqDto.getContent())
                    .view(0L)
                    .build();
        }
        else if(postCreateReqDto.getPostType().equals("review")){ // 리뷰
            Review review = Review.builder()
                    .title(postCreateReqDto.getTitle())
                    .content(postCreateReqDto.getContent())
                    .view(0L)
                    .build();
            Policy policy = policyRepository.findById(postCreateReqDto.getPolicyId()).orElseThrow(PolicyNotFoundException::new);
            review.setPolicy(policy);
            post = review;
        }
        else throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);

        post.setWriter(writer);
        Post savedPost = postRepository.save(post);

        if(fileList!=null&&!fileList.isEmpty()) {
            List<String> imageUrlList = imageService.uploadMultiFiles(fileList);
            imageService.savePostImageList(imageUrlList, savedPost);
        }
        log.info("게시글 생성 성공, postId = {}", savedPost.getId());
        return savedPost.toPostRepDto();
    }

    @Override
    public PostRepDto createPostTest(PostCreateTestReqDto postCreateReqDto, Member writer) throws IOException {
        Post post;
        if(postCreateReqDto.getPostType().equals("post")){ // 자유글
            post = Post.builder()
                    .title(postCreateReqDto.getTitle())
                    .contents(postCreateReqDto.getContentList())
                    .view(0L)
                    .build();
        }
        else if(postCreateReqDto.getPostType().equals("review")){ // 리뷰
            Review review = Review.builder()
                    .title(postCreateReqDto.getTitle())
                    .contents(postCreateReqDto.getContentList())
                    .view(0L)
                    .build();
            Policy policy = policyRepository.findById(postCreateReqDto.getPolicyId()).orElseThrow(PolicyNotFoundException::new);
            review.setPolicy(policy);
            post = review;
        }
        else throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);

        post.setWriter(writer);
        Post savedPost = postRepository.save(post);

        // 이미지 리스트 추출 후 기존 PostImage 매핑
        imageService.mappingPostImage(extractImageUrl(post),post);
        log.info("게시글 생성 성공, postId = {}", savedPost.getId());
        return savedPost.toPostRepDto();
    }

    @Override
    public PostRepDto updatePost(Long postId, PostUpdateReqDto postUpdateReqDto, List<MultipartFile> fileList, Member writer) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter()==null || post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
            throw new BusinessException(BaseResponseCode.POST_ACCESS_DENIED);
        }

        Post updatedPost = post.toBuilder()
                .title(postUpdateReqDto.getTitle())
                .content(postUpdateReqDto.getContent())
                .build();
        if(post instanceof Review){ // 리뷰이면
            if(postUpdateReqDto.getPolicyId()!=null&&!postUpdateReqDto.getPolicyId().isEmpty()){
                Policy policy = policyRepository.findById(postUpdateReqDto.getPolicyId()).orElseThrow(PolicyNotFoundException::new);
                ((Review)updatedPost).setPolicy(policy);
            }
        }
        Post savedPost = postRepository.save(updatedPost);

        if(fileList!=null&&!fileList.isEmpty()) {
            List<String> imageUrlList = imageService.uploadMultiFiles(fileList);
            imageService.savePostImageList(imageUrlList, savedPost);
        }
        if(postUpdateReqDto.getDeletedImgUrlList()!=null&&!postUpdateReqDto.getDeletedImgUrlList().isEmpty()){
            imageService.deleteMultiFile(postUpdateReqDto.getDeletedImgUrlList());
        }
        log.info("게시글 수정 성공, postId = {}", savedPost.getId());
        return savedPost.toPostRepDto();
    }

    @Override
    public PostRepDto updatePostTest(Long postId, PostUpdateReqTestDto postUpdateReqDto, Member writer) throws IOException {
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
        return savedPost.toPostRepDto();
    }

    @Override
    public void deletePost(Long postId, Member writer) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter() == null || post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
            throw new BusinessException(BaseResponseCode.POST_ACCESS_DENIED);
        }
        imageService.deleteMultiFile(post.getImages().stream().map(PostImage::getImgUrl).toList());
        postRepository.delete(post);
        scrapRepository.deleteAllByItemIdAndItemType(post.getId().toString(),ItemType.POST);
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
