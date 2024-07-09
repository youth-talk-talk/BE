package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.dto.post.PostUpdateReqDto;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.service.image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        Post savedPost;
        if(postCreateReqDto.getPostType().equals("free")){ // 자유글
            Post post = Post.builder()
                    .title(postCreateReqDto.getTitle())
                    .content(postCreateReqDto.getContent())
                    .view(0L)
                    .build();
            post.setWriter(writer);
            savedPost = postRepository.save(post);
        }
        else if(postCreateReqDto.getPostType().equals("review")){ // 리뷰
            Policy policy = policyRepository.findById(postCreateReqDto.getPolicyId()).orElseThrow(PolicyNotFoundException::new);
            Review review = Review.builder()
                    .title(postCreateReqDto.getTitle())
                    .content(postCreateReqDto.getContent())
                    .view(0L)
                    .build();
            review.setWriter(writer);
            review.setPolicy(policy);
            savedPost = postRepository.save(review);
        }
        else throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);

        if(fileList!=null&&!fileList.isEmpty()) {
            List<String> imageUrlList = imageService.uploadMultiFile(fileList);
            imageService.saveImageList(imageUrlList, savedPost);
        }
        return toPostRepDto(savedPost, postCreateReqDto.getPostType());
    }

    @Override
    @Transactional
    public PostRepDto updatePost(PostUpdateReqDto postUpdateReqDto, List<MultipartFile> fileList, Member writer) {
        return null;
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Member writer) {

    }

    private PostRepDto toPostRepDto(Post post,String postType) {
        return PostRepDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .policyId(postType.equals("review") ? ((Review)post).getPolicy().getPolicyId() : null)
                .policyTitle(postType.equals("review") ? ((Review)post).getPolicy().getTitle() : null)
                .postType(postType)
                .writerId(post.getWriter().getId())
                .images(post.getImages())
                .build();
    }
}
