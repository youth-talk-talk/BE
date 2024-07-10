package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.dto.post.PostUpdateReqDto;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.BusinessException;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
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
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PolicyRepository policyRepository;
    private final ImageService imageService;

    @Override
    @Transactional
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
            List<String> imageUrlList = imageService.uploadMultiFile(fileList);
            imageService.saveImageList(imageUrlList, savedPost);
        }
        return toPostRepDto(savedPost);
    }

    @Override
    @Transactional
    public PostRepDto updatePost(Long postId,PostUpdateReqDto postUpdateReqDto, List<MultipartFile> fileList, Member writer) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
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
            List<String> imageUrlList = imageService.uploadMultiFile(fileList);
            imageService.saveImageList(imageUrlList, savedPost);
        }
        if(postUpdateReqDto.getDeletedImgUrlList()!=null&&!postUpdateReqDto.getDeletedImgUrlList().isEmpty()){
            imageService.deleteMultiFile(postUpdateReqDto.getDeletedImgUrlList());
        }

        return toPostRepDto(savedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Member writer) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(post.getWriter().getId()!=writer.getId()){ // 작성자가 아닐 경우
            throw new BusinessException(BaseResponseCode.POST_ACCESS_DENIED);
        }
        postRepository.delete(post);
    }

    private PostRepDto toPostRepDto(Post post) {
        return PostRepDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .policyId(post instanceof Review ? ((Review)post).getPolicy().getPolicyId() : null)
                .policyTitle(post instanceof Review ? ((Review)post).getPolicy().getTitle() : null)
                .postType(post instanceof Review ? "review" : "post")
                .writerId(post.getWriter().getId())
                .images(post.getImages())
                .build();
    }
}
