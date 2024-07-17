package com.server.youthtalktalk.service.comment;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PolicyComment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.dto.comment.CommentDto;
import com.server.youthtalktalk.dto.comment.CommentTypeDto;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PolicyRepository;
import com.server.youthtalktalk.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    CommentService commentService;

    @Autowired
    PolicyRepository policyRepository;

    @Test
    void 게시글_댓글_조회_성공() throws Exception {
        // given
        Post post = Post.builder().title("post1").content("post1_content").build();

        Member member1 = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        Member member2 = Member.builder().username("member2").nickname("member2").region(Region.SEOUL).role(Role.USER).build();
        List<Member> members = Arrays.asList(member1, member2);

        PostComment comment1 = PostComment.builder().content("comment1").build();
        PostComment comment2 = PostComment.builder().content("comment2").build();
        PostComment comment3 = PostComment.builder().content("comment3").build();
        List<Comment> comments = Arrays.asList(comment1, comment2, comment3);

        comment1.setWriter(member1);
        comment2.setWriter(member2);

        comment1.setPost(post);
        comment2.setPost(post);
        comment3.setPost(post);

        postRepository.save(post);
        memberRepository.saveAll(members);
        commentRepository.saveAll(comments);

        // when
        List<Comment> commentList = commentService.getAllComments(
                new CommentTypeDto(Optional.empty(), Optional.ofNullable(post.getId())));
        List<CommentDto> commentDtoList = commentService.convertToDto(commentList);

        // then
        assertThat(commentList).hasSize(3);
        assertThat(commentDtoList).hasSize(3);
        assertThat(commentDtoList.get(0).nickname()).isEqualTo(comment1.getWriter().getNickname());
        assertThat(commentDtoList.get(0).content()).isEqualTo(comment1.getContent());
        assertThat(commentDtoList.get(1).nickname()).isEqualTo(comment2.getWriter().getNickname());
        assertThat(commentDtoList.get(1).content()).isEqualTo(comment2.getContent());
        assertThat(commentDtoList.get(2).nickname()).isEqualTo("null"); // (알수없음) 댓글
        assertThat(commentDtoList.get(2).content()).isEqualTo(comment3.getContent());
    }

    @Test
    void 정책_댓글_조회_성공() {
        // given
        Policy policy = Policy.builder().policyId("policy1").build();

        Member member1 = Member.builder().username("member1").nickname("member1").region(Region.SEOUL).role(Role.USER).build();
        Member member2 = Member.builder().username("member2").nickname("member2").region(Region.SEOUL).role(Role.USER).build();
        List<Member> members = Arrays.asList(member1, member2);

        PolicyComment comment1 = PolicyComment.builder().content("comment1").build();
        PolicyComment comment2 = PolicyComment.builder().content("comment2").build();
        PolicyComment comment3 = PolicyComment.builder().content("comment3").build();
        List<Comment> comments = Arrays.asList(comment1, comment2, comment3);

        comment1.setWriter(member1);
        comment2.setWriter(member2);

        comment1.setPolicy(policy);
        comment2.setPolicy(policy);
        comment3.setPolicy(policy);

        policyRepository.save(policy);
        memberRepository.saveAll(members);
        commentRepository.saveAll(comments);

        // when
        List<Comment> commentList = commentService.getAllComments(
                new CommentTypeDto(Optional.ofNullable(policy.getPolicyId()), Optional.empty()));
        List<CommentDto> commentDtoList = commentService.convertToDto(commentList);

        // then
        assertThat(commentList).hasSize(3);
        assertThat(commentDtoList).hasSize(3);
        assertThat(commentDtoList.get(0).nickname()).isEqualTo(comment1.getWriter().getNickname());
        assertThat(commentDtoList.get(0).content()).isEqualTo(comment1.getContent());
        assertThat(commentDtoList.get(1).nickname()).isEqualTo(comment2.getWriter().getNickname());
        assertThat(commentDtoList.get(1).content()).isEqualTo(comment2.getContent());
        assertThat(commentDtoList.get(2).nickname()).isEqualTo("null"); // (알수없음) 댓글
        assertThat(commentDtoList.get(2).content()).isEqualTo(comment3.getContent());
    }

    @Test
    public void postId_policyId_모두_없으면_400() throws Exception {
        // given
        CommentTypeDto commentTypeDto = new CommentTypeDto(Optional.empty(), Optional.empty());

        // when, then
        assertThrows(InvalidValueException.class, () -> commentService.getAllComments(commentTypeDto));
    }

    @Test
    public void postId_policyId_모두_있으면_400() throws Exception {
        // given
        CommentTypeDto commentTypeDto = new CommentTypeDto(Optional.of("policyId"), Optional.of(1L));

        // when, then
        assertThrows(InvalidValueException.class, () -> commentService.getAllComments(commentTypeDto));
    }
}