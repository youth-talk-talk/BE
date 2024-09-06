package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.comment.Comment;
import com.server.youthtalktalk.domain.comment.PostComment;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.repository.CommentRepository;
import com.server.youthtalktalk.repository.MemberRepository;
import com.server.youthtalktalk.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberService memberService;

    @Test
    void 회원탈퇴_성공_게시글과_댓글_모두_있음() {
        // given
        Member member = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member);

        List<Post> posts = new ArrayList<>();
        Post post1 = Post.builder().title("post1").content("post1_content").build();
        Post post2 = Post.builder().title("post2").content("post2_content").build();
        post1.setWriter(member);
        post2.setWriter(member);
        posts.add(post1);
        posts.add(post2);
        postRepository.saveAll(posts);

        List<Comment> comments = new ArrayList<>();
        Comment comment1 = PostComment.builder().content("comment1").build();
        Comment comment2 = PostComment.builder().content("comment2").build();
        comment1.setWriter(member);
        comment2.setWriter(member);
        comments.add(comment1);
        comments.add(comment2);
        commentRepository.saveAll(comments);

        // when
        memberService.deleteMember(member,null);

        // then
        assertThat(memberRepository.findById(member.getId())).isNotPresent();

        for(Post post : posts) {
            assertThat(postRepository.findById(post.getId())).isPresent();
            assertThat(post.getWriter()).isNull();
            assertThat(post.getContent()).isNotEmpty();
        }

        for (Comment comment : comments) {
            assertThat(commentRepository.findById(comment.getId())).isPresent();
            assertThat(comment.getWriter()).isNull();
            assertThat(comment.getContent()).isNotEmpty();
        }
    }

    @Test
    void 회원탈퇴_성공_게시글만_있음() {
        // given
        Member member = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member);

        List<Post> posts = new ArrayList<>();
        Post post1 = Post.builder().title("post1").content("post1_content").build();
        Post post2 = Post.builder().title("post2").content("post2_content").build();
        post1.setWriter(member);
        post2.setWriter(member);
        posts.add(post1);
        posts.add(post2);
        postRepository.saveAll(posts);

        // when
        memberService.deleteMember(member,null);

        // then
        assertThat(memberRepository.findById(member.getId())).isNotPresent();

        for(Post post : posts) {
            assertThat(postRepository.findById(post.getId())).isPresent();
            assertThat(post.getWriter()).isNull();
            assertThat(post.getContent()).isNotEmpty();
        }
    }

    @Test
    void 회원탈퇴_성공_댓글만_있음() {
        // given
        Member member = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member);

        List<Comment> comments = new ArrayList<>();
        Comment comment1 = PostComment.builder().content("comment1").build();
        Comment comment2 = PostComment.builder().content("comment2").build();
        comment1.setWriter(member);
        comment2.setWriter(member);
        comments.add(comment1);
        comments.add(comment2);
        commentRepository.saveAll(comments);

        // when
        memberService.deleteMember(member,null);

        // then
        assertThat(memberRepository.findById(member.getId())).isNotPresent();

        for (Comment comment : comments) {
            assertThat(commentRepository.findById(comment.getId())).isPresent();
            assertThat(comment.getWriter()).isNull();
            assertThat(comment.getContent()).isNotEmpty();
        }

    }
}