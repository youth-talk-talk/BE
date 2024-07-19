package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void 정책_댓글_생성_없는_policyId_400() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        // when, then
        assertThrows(PolicyNotFoundException.class,
                () -> commentService.createPolicyComment("notPolicyId", "content", member));
    }

    @Test
    void 게시글_댓글_생성_없는_postId_400() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        // when, then
        assertThrows(PostNotFoundException.class,
                () -> commentService.createPostComment(1000L, "content", member));
    }

    @Test
    void 정책_댓글_조회_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        Policy policy = Policy.builder().policyId("newPolicy").build();
        policyRepository.save(policy);

        PolicyComment policyComment1 = PolicyComment.builder().policy(policy).content("content1").writer(member).build();
        commentRepository.save(policyComment1);
        PolicyComment policyComment3 = PolicyComment.builder().policy(policy).content("content3").writer(member).build();
        commentRepository.save(policyComment3);
        PolicyComment policyComment2 = PolicyComment.builder().policy(policy).content("content2").writer(member).build();
        commentRepository.save(policyComment2);

        // when
        List<PolicyComment> policyComments = commentService.getPolicyComments(policy.getPolicyId());

        // then
        assertThat(policyComments.size()).isEqualTo(3);
        assertThat(policyComments.get(1).getId()).isEqualTo(policyComment3.getId()); // 시간순 정렬 검증
    }

    @Test
    void 게시글_댓글_조회_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        Post post = Post.builder().build();
        postRepository.save(post);

        PostComment postComment1 = PostComment.builder().post(post).writer(member).content("content1").build();
        commentRepository.save(postComment1);
        PostComment postComment3 = PostComment.builder().post(post).writer(member).content("content1").build();
        commentRepository.save(postComment3);
        PostComment postComment2 = PostComment.builder().post(post).writer(member).content("content1").build();
        commentRepository.save(postComment2);

        // when
        List<PostComment> postComments = commentService.getPostComments(post.getId());

        // then
        assertThat(postComments.size()).isEqualTo(3);
        assertThat(postComments.get(1).getId()).isEqualTo(postComment3.getId()); // 시간순 정렬 검증
    }

    @Test
    void 댓글_수정_성공() throws Exception {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Comment comment = Comment.builder().content("content").build();
        comment.setWriter(member);

        memberRepository.save(member);
        commentRepository.save(comment);

        // when
        commentService.updateComment(comment.getId(), "new_content");

        // then
        Comment savedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(savedComment.getWriter()).isEqualTo(member);
        assertThat(savedComment.getContent().equals("new_content")).isTrue();
        assertThat(member.getComments().size()).isEqualTo(1);
        assertThat(member.getComments().get(0).getId()).isEqualTo(savedComment.getId());
        assertThat(member.getComments().get(0).getContent().equals("new_content")).isTrue();
    }

    @Test
    void 댓글_삭제_성공() throws Exception {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Comment comment = Comment.builder().content("content").build();
        comment.setWriter(member);

        memberRepository.save(member);
        commentRepository.save(comment);

        // when
        commentService.deleteComment(comment.getId());

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        assertThat(member.getComments().contains(comment)).isFalse();

    }

}