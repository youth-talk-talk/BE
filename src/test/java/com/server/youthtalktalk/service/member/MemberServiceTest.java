package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.member.entity.SocialType;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.member.dto.SignUpRequestDto;
import com.server.youthtalktalk.global.response.exception.member.BlockDuplicatedException;
import com.server.youthtalktalk.global.response.exception.member.InvalidMemberForBlockException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.response.exception.member.NotBlockedMemberException;
import com.server.youthtalktalk.global.util.HashUtil;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    BlockRepository blockRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    HashUtil hashUtil;

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator(); // Validator 설정
    }

    @Test
    public void 회원가입_성공() {
        // given
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .socialId("1111").socialType("kakao").nickname("member1").region("부산").build();

        // when
        Long memberId = memberService.signUp(signUpRequestDto);

        // then
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        assertThat(member.getUsername()).isEqualTo(hashUtil.hash(signUpRequestDto.getSocialId()));
        assertThat(member.getRefreshToken()).isNotBlank();
        assertThat(member.getNickname()).isEqualTo(signUpRequestDto.getNickname());
        assertThat(member.getSocialType()).isEqualTo(SocialType.KAKAO);
        assertThat(member.getRegion()).isEqualTo(Region.BUSAN);
    }

    @Test
    public void 회원가입_실패_NotBlank_검증_오류() {
        // given
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .socialId(null).socialType("kakao").nickname("member1").region("부산").build();

        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .socialId("").socialType("kakao").nickname("member1").region("부산").build();

        // when
        Set<ConstraintViolation<SignUpRequestDto>> violations1 = validator.validate(signUpRequestDto1);
        Set<ConstraintViolation<SignUpRequestDto>> violations2 = validator.validate(signUpRequestDto2);

        // then
        assertFalse(violations1.isEmpty());
        assertThat(violations1.size()).isEqualTo(1);
        assertThat(violations1.iterator().next().getMessage()).isEqualTo("socialId는 필수값입니다.");

        assertFalse(violations2.isEmpty());
        assertThat(violations2.size()).isEqualTo(1);
        assertThat(violations2.iterator().next().getMessage()).isEqualTo("socialId는 필수값입니다.");
    }

    @Test
    @DisplayName("회원가입 실패_닉네임 길이 제한 초과")
    public void testSignUp1() {
        // given
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .socialId("11111").socialType("kakao").nickname("여덟글자보다긴닉네임").region("부산").build();

        // when
        Set<ConstraintViolation<SignUpRequestDto>> violations1 = validator.validate(signUpRequestDto1);

        // then
        assertFalse(violations1.isEmpty());
        assertThat(violations1.size()).isEqualTo(1);
        assertThat(violations1.iterator().next().getMessage()).isEqualTo("닉네임 길이는 1자 이상 8자 이하여야 합니다.");
    }

    @Test
    @DisplayName("회원가입 실패_닉네임에 허용되지 않은 글자 포함")
    public void testSignUp2() {
        // given
        SignUpRequestDto signUpRequestDto1 = SignUpRequestDto.builder()
                .socialId("11111").socialType("kakao").nickname("member1@").region("서울").build(); // 닉네임에 특수문자 포함
        SignUpRequestDto signUpRequestDto2 = SignUpRequestDto.builder()
                .socialId("22222").socialType("kakao").nickname("member2 ").region("서울").build(); // 닉네임에 공백 포함

        // when
        Set<ConstraintViolation<SignUpRequestDto>> violations1 = validator.validate(signUpRequestDto1);
        Set<ConstraintViolation<SignUpRequestDto>> violations2 = validator.validate(signUpRequestDto2);

        // then
        assertFalse(violations1.isEmpty());
        assertThat(violations1.size()).isEqualTo(1);
        assertThat(violations1.iterator().next().getMessage()).isEqualTo("닉네임은 한글과 영어(대소문자) 및 숫자만 가능하며, 공백과 특수문자는 사용할 수 없습니다.");

        assertFalse(violations2.isEmpty());
        assertThat(violations2.size()).isEqualTo(1);
        assertThat(violations2.iterator().next().getMessage()).isEqualTo("닉네임은 한글과 영어(대소문자) 및 숫자만 가능하며, 공백과 특수문자는 사용할 수 없습니다.");
    }

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

    @Test
    void 회원_차단_성공() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        Member member2 = Member.builder().username("username2").role(Role.USER).build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        // member1이 member2를 차단
        memberService.blockMember(member1, member2.getId());

        assertThat(memberRepository.findById(member1.getId())).isPresent();
        assertThat(memberRepository.findById(member2.getId())).isPresent();
        assertThat(blockRepository.findByMemberAndBlockedMember(member1, member2)).isPresent();
        assertThat(member1.getBlocks().size()).isEqualTo(1);
        assertThat(member2.getBlocks().size()).isEqualTo(0);
    }

    @Test
    void 중복_차단_예외() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        Member member2 = Member.builder().username("username2").role(Role.USER).build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberService.blockMember(member1, member2.getId()); // member1이 member2를 차단

        assertThrows(BlockDuplicatedException.class,
                () -> memberService.blockMember(member1, member2.getId())); // 중복 차단 시 예외 발생해야함

    }

    @Test
    void 존재하지않는_회원_차단_예외() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member1);

        assertThrows(MemberNotFoundException.class,
                () -> memberService.blockMember(member1, 0L));// 존재하지 않는 회원을 차단 시 예외 발생해야함
    }

    @Test
    void 차단할수없는_회원_차단_예외() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member1);

        assertThrows(InvalidMemberForBlockException.class,
                () -> memberService.blockMember(member1, member1.getId()));// 스스로를 차단할 경우 예외 발생해야함
    }

    @Test
    void 회원_차단_해제_성공() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        Member member2 = Member.builder().username("username2").role(Role.USER).build();
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberService.blockMember(member1, member2.getId()); // member1이 member2를 차단

        memberService.unblockMember(member1, member2.getId()); // member1이 member2를 차단 해제

        assertThat(blockRepository.findByMemberAndBlockedMember(member1, member2)).isNotPresent();
        assertThat(member1.getBlocks().size()).isEqualTo(0);
        assertThat(member2.getBlocks().size()).isEqualTo(0);
    }

    @Test
    void 존재하지않는_회원_차단해제_예외() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        memberRepository.save(member1);

        assertThrows(MemberNotFoundException.class,
                () -> memberService.unblockMember(member1, 0L));// 존재하지 않는 회원을 차단 해제 시 예외 발생해야함
    }

    @Test
    void 차단하지않은_회원_차단해제_예외() {
        Member member1 = Member.builder().username("username1").role(Role.USER).build();
        Member member2 = Member.builder().username("username2").role(Role.USER).build();
        memberRepository.save(member1);
        memberRepository.save(member2); // member1은 member2를 차단하지 않음

        assertThrows(NotBlockedMemberException.class,
                () -> memberService.unblockMember(member1, member2.getId()));// 차단하지 않은 회원을 차단 해제 시 예외 발생해야함
    }
}