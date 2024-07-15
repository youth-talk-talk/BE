package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class PostReadServiceTest {
    @Autowired
    private PostReadService postReadService;
    @Autowired
    private EntityManager em;

    private Member member;
    private Post post;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
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
        this.post = postRepository.save(Post.builder()
                        .title("test")
                        .view(0L)
                        .content("content")
                        .writer(member)
                        .build());
        clear();
    }

    @Test
    @DisplayName("게시글 상세 조회")
    void getPostByIdTest(){
        PostRepDto postRepDto = postReadService.getPostById(post.getId(),member);
        assertThat(postRepDto.getPostId()).isEqualTo(post.getId());
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회")
    void getAllMyPostTest(){
        Pageable pageable = PageRequest.of(0,10);
        assertThat(postReadService.getAllMyPost(pageable,member)).hasSize(1);
    }

    @Test
    @DisplayName("내가 스크랩한 게시글 조회")
    void getScrapPostListTest(){
        scrapRepository.save(Scrap.builder()
                        .itemId(post.getId().toString())
                        .itemType(ItemType.POST)
                        .member(member)
                        .build());
        Pageable pageable = PageRequest.of(0,10);
        assertThat(postReadService.getScrapPostList(pageable,member).size()).isEqualTo(1);
    }
}
