package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.likes.entity.Likes;
import com.server.youthtalktalk.domain.comment.service.CommentService;
import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PolicyComment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.likes.repository.LikeRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.RepeatCode;
import com.server.youthtalktalk.domain.policy.entity.condition.Earn;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.global.response.exception.policy.PolicyNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
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
    LikeRepository likeRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    EntityManager em;

    /**
     * TODO 조회 관련 테스트 코드는 CommentReadServiceTest로 옮겨야 함
     */

    @Test
    void 정책_댓글_생성_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        memberRepository.save(member);

        Policy policy = createPolicy("policyNum");
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

        Policy reloadedPolicy = policyRepository.findByPolicyId(policy.getPolicyId()).orElseThrow();
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
                () -> commentService.createPolicyComment(123456L, "content", member));
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
    void 정책_댓글_수정_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Policy policy = createPolicy("policyNum");
        PolicyComment comment = PolicyComment.builder().content("content").build();
        comment.setWriter(member);
        comment.setPolicy(policy);

        memberRepository.save(member);
        policyRepository.save(policy);
        commentRepository.save(comment);

        // when
        commentService.updateComment(comment.getId(), "new_content");

        // then
        Comment savedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(savedComment.getWriter()).isEqualTo(member);
        assertThat(savedComment.getRelatedEntityId()).isEqualTo(policy.getPolicyId());
        assertThat(savedComment.getContent().equals("new_content")).isTrue();

        assertThat(member.getComments().size()).isEqualTo(1);
        assertThat(member.getComments().get(0).getId()).isEqualTo(savedComment.getId());
        assertThat(member.getComments().get(0).getContent()).isEqualTo("new_content");

        assertThat(policy.getPolicyComments().size()).isEqualTo(1);
        assertThat(policy.getPolicyComments().get(0).getId()).isEqualTo(savedComment.getId());
        assertThat(policy.getPolicyComments().get(0).getContent()).isEqualTo("new_content");
    }

    @Test
    void 게시글_댓글_수정_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Post post = Post.builder().title("post1").content("post1_content").build();
        PostComment comment = PostComment.builder().content("content").build();
        comment.setWriter(member);
        comment.setPost(post);

        memberRepository.save(member);
        postRepository.save(post);
        commentRepository.save(comment);

        // when
        commentService.updateComment(comment.getId(), "new_content");

        // then
        Comment savedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(savedComment.getWriter()).isEqualTo(member);
        assertThat(savedComment.getRelatedEntityId()).isEqualTo(post.getId());
        assertThat(savedComment.getContent().equals("new_content")).isTrue();

        assertThat(member.getComments().size()).isEqualTo(1);
        assertThat(member.getComments().get(0).getId()).isEqualTo(savedComment.getId());
        assertThat(member.getComments().get(0).getContent()).isEqualTo("new_content");

        assertThat(post.getPostComments().size()).isEqualTo(1);
        assertThat(post.getPostComments().get(0).getId()).isEqualTo(savedComment.getId());
        assertThat(post.getPostComments().get(0).getContent()).isEqualTo("new_content");
    }


    @Test
    void 댓글_삭제_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Comment comment = PostComment.builder().content("content").build();
        comment.setWriter(member);

        memberRepository.save(member);
        commentRepository.save(comment);

        // when
        commentService.deleteComment(comment.getId());

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        assertThat(member.getComments().contains(comment)).isFalse();

    }

    @Test
    void 회원이_작성한_댓글_조회_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        memberRepository.save(member);

        Comment comment1 = PostComment.builder().content("content1").build();
        Comment comment2 = PostComment.builder().content("content2").build();
        comment1.setWriter(member);
        comment2.setWriter(member);
        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // when
        List<Comment> comments = commentService.getMyComments(member);

        // then
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.contains(comment1)).isTrue();
        assertThat(comments.contains(comment2)).isTrue();
        assertThat(comments.get(0).getId()).isEqualTo(comment2.getId()); // 최신순 정렬 검증
    }

    @Test
    void 회원이_작성한_댓글_조회_성공_작성한_댓글이_없음() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        memberRepository.save(member);

        // when
        List<Comment> comments = commentService.getMyComments(member);

        // then
        assertThat(comments.isEmpty()).isTrue();

    }

    @Test
    void 댓글_좋아요_등록_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        memberRepository.save(member);

        Comment comment = PostComment.builder().content("content1").build();
        commentRepository.save(comment);

        // when
        commentService.setCommentLiked(comment.getId(), member);

        // then
        Likes like = likeRepository.findByMemberAndComment(member, comment).orElseThrow();
        assertThat(like.getMember()).isEqualTo(member);
        assertThat(like.getComment()).isEqualTo(comment);

        assertThat(member.getLikes().contains(like)).isTrue();
        assertThat(comment.getCommentLikes().contains(like)).isTrue();
    }

    @Test
    void 댓글_좋아요_해제_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Comment comment = PostComment.builder().content("content1").build();
        Likes like = Likes.builder().build();
        like.setMember(member);
        like.setComment(comment);

        memberRepository.save(member);
        commentRepository.save(comment);
        likeRepository.save(like);

        // when
        commentService.setCommentUnliked(comment.getId(), member);

        // then
        assertThat(likeRepository.findById(like.getId())).isNotPresent();
        assertThat(!member.getLikes().contains(like)).isTrue();
        assertThat(!comment.getCommentLikes().contains(like)).isTrue();
    }

    @Test
    void 회원이_좋아요한_모든_댓글_조회_성공() {
        // given
        Member member = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).build();
        Comment comment1 = PostComment.builder().content("content1").build();
        Comment comment2 = PostComment.builder().content("content2").build();
        Likes like1 = Likes.builder().build();
        Likes like2 = Likes.builder().build();
        like1.setMember(member);
        like2.setMember(member);
        like1.setComment(comment1);
        like2.setComment(comment2);

        memberRepository.save(member);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        likeRepository.save(like1);
        likeRepository.save(like2);

        // when
        List<Comment> likedComments = commentService.getLikedComments(member);

        // then
        assertThat(likedComments.size()).isEqualTo(2);
        assertThat(likedComments.contains(comment1)).isTrue();
        assertThat(likedComments.contains(comment2)).isTrue();
    }

    private static Policy createPolicy(String policyNum) {
        return Policy.builder()
                .title("test")
                .policyNum(policyNum)
                .repeatCode(RepeatCode.ALWAYS)
                .earn(Earn.UNRESTRICTED)
                .marriage(Marriage.UNRESTRICTED)
                .institutionType(InstitutionType.CENTER)
                .region(Region.CENTER)
                .build();
    }

}