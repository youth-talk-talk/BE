package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.PostCreateReqDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceImplTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private EntityManager em;

    private Member member;
    private Policy policy;

    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    void init(){
        this.member = memberRepository.save(Member.builder()
                .username("testPost")
                .role(Role.USER)
                .build());
        this.policy = policyRepository.save(Policy.builder()
                        .policyId("testId")
                        .title("testPolicy")
                        .category(Category.JOB)
                        .build());
        clear();
    }

    @Test
    @DisplayName("상속 관계 매핑 테스트")
    void inheritanceTest() throws Exception {
        Policy policy = Policy.builder()
                .title("test")
                .category(Category.JOB)
                .build();

        Review review = Review.builder()
                .title("test")
                .content("test")
                .view(0L)
                .build();
        Post free = Post.builder()
                .title("post")
                .build();

        review.setPolicy(policy);
        Post post = review;
        assertThat(((Review)post).getPolicy().getTitle()).isEqualTo("test");
    }

    @Test
    @DisplayName("자유글 생성 성공 테스트")
    void createPostTest() throws IOException {
        PostCreateReqDto postCreateReqDto = PostCreateReqDto.builder()
                .policyId(null)
                .postType("free")
                .content("test")
                .title("test")
                .build();
        PostRepDto result = postService.createPost(postCreateReqDto,null,this.member);
        assertThat(result.getPostType()).isEqualTo("free");
        assertThat(result.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("리뷰 생성 성공 테스트")
    void createReviewTest() throws IOException {
        PostCreateReqDto postCreateReqDto = PostCreateReqDto.builder()
                .policyId(this.policy.getPolicyId())
                .postType("review")
                .content("test")
                .title("test")
                .build();
        PostRepDto result = postService.createPost(postCreateReqDto,null,this.member);
        assertThat(result.getPostType()).isEqualTo("review");
        assertThat(result.getContent()).isEqualTo("test");
    }

}