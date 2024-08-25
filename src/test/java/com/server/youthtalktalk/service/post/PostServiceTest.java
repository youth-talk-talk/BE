package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
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
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceTest {

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
    private Post post;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ScrapRepository scrapRepository;

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
        this.post = postRepository.save(Post.builder()
                .title("test")
                .writer(member)
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
        review.setPolicy(policy);
        Post post = review;
        Post savedPost = postRepository.save(post);
        Post free = Post.builder()
                .title("post")
                .build();

        assertThat(((Review)savedPost).getPolicy().getTitle()).isEqualTo("test");
        System.out.println("instance : "+(savedPost instanceof Review));
    }

    @Test
    @DisplayName("자유글 생성 성공 테스트")
    void createPostTest() throws IOException {
        PostCreateReqDto postCreateReqDto = PostCreateReqDto.builder()
                .policyId(null)
                .postType("post")
                .content("test")
                .title("test")
                .build();
        PostRepDto result = postService.createPost(postCreateReqDto,null,this.member);
        assertThat(result.getPostType()).isEqualTo("post");
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

//    @Test
//    @DisplayName("리뷰 수정 성공 테스트")
//    void updateReviewTest() throws IOException {
//        PostUpdateReqDto postUpdateReqDto = PostUpdateReqDto.builder()
//                .title("update")
//                .content("test")
//                .build();
//        PostRepDto postRepDto = postService.updatePost(752L,postUpdateReqDto,null,this.member);
//        assertThat(postRepDto.getTitle()).isEqualTo("update");
//    }

    @Test
    @DisplayName("게시글 스크랩 테스트")
    void scrapPostTest(){
        postService.scrapPost(post.getId(), member); // 스크랩
        assertThat(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), post.getId().toString(), ItemType.POST)).isTrue();

        postService.scrapPost(post.getId(), member); // 스크랩 취소
        assertThat(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), post.getId().toString(), ItemType.POST)).isFalse();
    }
}