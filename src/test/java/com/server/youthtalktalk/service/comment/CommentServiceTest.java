package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PolicyRepository policyRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    EntityManager em;

    @Test
    void 정책_댓글_생성_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        Policy policy = Policy.builder().policyId("newPolicy").build();
        policyRepository.save(policy);

        String content = "policyComment_content";

        // when
        PolicyComment policyComment = commentService.createPolicyComment(policy.getPolicyId(), content, member);

        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findById(policyComment.getId())).isPresent();
        assertThat(policyComment.getWriter()).isEqualTo(member);
        assertThat(policyComment.getContent()).isEqualTo(content);
        assertThat(policyComment.getPolicy().getPolicyId()).isEqualTo(policy.getPolicyId());

        Policy reloadedPolicy = policyRepository.findById(policy.getPolicyId()).orElseThrow();
        assertThat(reloadedPolicy.getPolicyComments().size()).isEqualTo(1);
        assertThat(member.getComments().size()).isEqualTo(1);
    }

    @Test
    void 게시글_댓글_생성_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        Post post = Post.builder().title("post1").content("post1_content").build();
        postRepository.save(post);

        String content = "postComment_content";

        // when
        PostComment postComment = commentService.createPostComment(post.getId(), content, member);

        em.flush();
        em.clear();

        // then
        assertThat(commentRepository.findById(postComment.getId())).isPresent();
        assertThat(postComment.getWriter()).isEqualTo(member);
        assertThat(postComment.getContent()).isEqualTo(content);
        assertThat(postComment.getPost().getId()).isEqualTo(post.getId());

        Post reloadPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(reloadPost.getPostComments().size()).isEqualTo(1);
        assertThat(member.getComments().size()).isEqualTo(1);
    }

}