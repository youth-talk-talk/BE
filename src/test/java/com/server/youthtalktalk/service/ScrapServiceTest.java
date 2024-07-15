package com.server.youthtalktalk.service;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import com.server.youthtalktalk.service.scrap.ScrapService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class ScrapServiceTest {
    @Autowired
    private ScrapService scrapService;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PolicyRepository policyRepository;
//    @Autowired
//    private EntityManager em;

    Member member;
    Post post;
    Policy policy;

    @BeforeEach
    void init() {
        this.member = memberRepository.save(Member.builder()
                .username("testPost")
                .role(Role.USER)
                .build());
        this.post = postRepository.save(Post.builder()
                .title("test")
                .view(0L)
                .content("test")
                .writer(member)
                .build());
        this.policy = policyRepository.save(Policy.builder()
                .title("test")
                .policyId("test")
                .build());
    }

    @Test
    @DisplayName("게시글 스크랩 테스트")
    void scrapPostTest(){
        // 스크랩
        scrapService.scrapPost(post.getId(),member);
        Scrap scrap = scrapRepository.findByMemberAndItemIdAndItemType(member,post.getId().toString(), ItemType.POST).get();
        assertThat(scrap.getItemId()).isEqualTo(post.getId().toString());
        assertThat(scrap.getItemType()).isEqualTo(ItemType.POST);

        // 스크랩 취소
        scrapService.scrapPost(post.getId(),member);
        assertThat(scrapRepository.findById(scrap.getId())).isNotPresent();
    }

    @Test
    @DisplayName("정책 스크랩 테스트")
    void scrapPolicyTest(){
        // 스크랩
        scrapService.scrapPolicy(policy.getPolicyId(),member);
        Scrap scrap = scrapRepository.findByMemberAndItemIdAndItemType(member,policy.getPolicyId(), ItemType.POLICY).get();
        assertThat(scrap.getItemId()).isEqualTo(policy.getPolicyId());
        assertThat(scrap.getItemType()).isEqualTo(ItemType.POLICY);

        // 스크랩 취소
        scrapService.scrapPost(post.getId(),member);
        assertThat(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),policy.getPolicyId(),ItemType.POLICY)).isFalse();
    }

    @Test
    @DisplayName("연관관계 편의 메서드 테스트")
    void jpaTest(){
        post.setWriter(member);
        assertThat(member.getPosts().size()).isEqualTo(1);

        postRepository.delete(post);
        assertThat(member.getPosts().size()).isEqualTo(0);
    }
}
