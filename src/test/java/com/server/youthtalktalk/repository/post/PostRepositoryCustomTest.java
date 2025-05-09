package com.server.youthtalktalk.repository.post;

import com.server.youthtalktalk.config.TestQueryDSLConfig;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.RepeatCode;
import com.server.youthtalktalk.domain.policy.entity.condition.Earn;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestQueryDSLConfig.class)
public class PostRepositoryCustomTest {
    @Autowired
    private PostRepositoryCustom postRepositoryCustom;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private BlockRepository blockRepository;

    private static final long LEN = 2;
    private static final int TOP = 5;
    Member member1, member2;
    Policy policy;
    List<Post> postList = new ArrayList<>();

    @BeforeEach
    void init(){
        member1 = memberRepository.save(Member.builder()
                .id(1L)
                .username("member1")
                .role(Role.USER)
                .build());
        member2 = memberRepository.save(Member.builder()
                .id(2L)
                .username("member2")
                .role(Role.USER)
                .build());

        this.policy = policyRepository.save(Policy.builder()
                .policyNum("policyNum")
                .title("policy1")
                .category(Category.JOB)
                .repeatCode(RepeatCode.PERIOD)
                .earn(Earn.UNRESTRICTED)
                .institutionType(InstitutionType.CENTER)
                .region(Region.CENTER)
                .marriage(Marriage.UNRESTRICTED)
                .build());

        List<Content> contentList = List.of(Content.builder().content("policies").type(ContentType.TEXT).build());
        for(long i = 1; i <= LEN; i++) {
            postList.add(postRepository.save(Post.builder()
                .title("post" + i)
                .writer(member1)
                .contents(contentList)
                .build()));

            postList.add(postRepository.save(Review.builder()
                .title("review" + i)
                .writer(member2)
                .policy(policy)
                .contents(contentList)
                .build()));
        }
    }

    @Test
    @DisplayName("전체 게시글 조회 성공")
    void successFindAllPosts(){
        // Given
        Pageable pageable1 = PageRequest.of(0, 1);
        Pageable pageable2 = PageRequest.of(1, 1);

        // When
        Page<Post> posts1 = postRepositoryCustom.findAllPosts(member1, pageable1);
        Page<Post> posts2 = postRepositoryCustom.findAllPosts(member1, pageable2);
        Post post1 = posts1.getContent().get(0);
        // Then
        assertThat(posts1.getSize()).isEqualTo(1);
        assertThat(posts2.getSize()).isEqualTo(1);
        assertThat(posts1.getTotalElements()).isEqualTo(2);
        assertThat(posts2.getTotalElements()).isEqualTo(2);

        assertThat(post1.getId()).isEqualTo(postList.get(2).getId());
        assertThat(post1.getTitle()).isEqualTo(postList.get(2).getTitle());
        assertThat(post1.getWriter()).isEqualTo(member1);
        assertThat(post1.getContents()).hasSize(1);
    }

    @Test
    @DisplayName("Top 조회수 인기글 조회 성공")
    void successFindTopPostsByView(){
        // Given
        Post post1 = postRepository.save(Post.builder().title("post1").view(3L).build());
        Post post2 = postRepository.save(Post.builder().title("post2").view(2L).build());
        Post post3 = postRepository.save(Post.builder().title("post3").view(2L).build());
        Post post4 = postRepository.save(Post.builder().title("post4").view(1L).build());

        // When
        List<Post> posts = postRepositoryCustom.findTopPostsByView(member1, TOP);
        Post[] arr = posts.toArray(new Post[0]);
        // Then
        assertThat(arr[0].getId()).isEqualTo(post1.getId());
        assertThat(arr[1].getId()).isEqualTo(post3.getId());
        assertThat(arr[2].getId()).isEqualTo(post2.getId());
        assertThat(arr[3].getId()).isEqualTo(post4.getId());
    }
}
