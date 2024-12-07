package com.server.youthtalktalk.domain.member.service;

import com.server.youthtalktalk.domain.comment.entity.Comment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.member.entity.SocialType;
import com.server.youthtalktalk.domain.policy.entity.Region;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.member.dto.MemberUpdateDto;
import com.server.youthtalktalk.domain.member.dto.SignUpRequestDto;
import com.server.youthtalktalk.domain.member.dto.apple.AppleDto;
import com.server.youthtalktalk.domain.member.dto.apple.AppleTokenResponseDto;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.global.response.exception.member.AppleTokenValidationException;
import com.server.youthtalktalk.global.response.exception.member.MemberAccessDeniedException;
import com.server.youthtalktalk.global.response.exception.member.MemberDuplicatedException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import com.server.youthtalktalk.global.util.HashUtil;
import com.server.youthtalktalk.domain.comment.repository.CommentRepository;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;
    private final AppleAuthUtil appleAuthUtil;
    private final HashUtil hashUtil;

    /**
     * 회원 가입
     */
    @Override
    public Long signUp(SignUpRequestDto signUpRequestDto) {
        String socialId = signUpRequestDto.getSocialId(); // 평문의 소셜 id
        String nickname = signUpRequestDto.getNickname();
        SocialType socialType = SocialType.fromString(signUpRequestDto.getSocialType());
        Region region = Region.fromRegionStr(signUpRequestDto.getRegion());

        String username = hashUtil.hash(socialId);
        checkIfDuplicatedMember(username); // 중복 회원 검증

        if (socialType == SocialType.APPLE){
            String idToken = signUpRequestDto.getIdToken();
            appleAuthUtil.verifyIdentityToken(idToken); // 애플 토큰 검증
        }

        Member member = Member.builder()
                .username(username)
                .nickname(nickname)
                .region(region)
                .socialType(socialType)
                .role(Role.USER)
                .build();

        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.updateRefreshToken(username, refreshToken);
        jwtService.sendAccessAndRefreshToken(httpServletResponse, accessToken, refreshToken);

        return member.getId();
    }

    /**
     * 현재 로그인한 사용자 조회
     */
    @Override
    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new MemberAccessDeniedException();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return memberRepository.findByUsername(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
    }

    /**
     * 회원정보 수정
     */
    @Override
    public void updateMemberInfo(MemberUpdateDto memberUpdateDto, Member member) {
        String updateNickname = memberUpdateDto.nickname();
        String updateRegion = memberUpdateDto.region();
        if (updateNickname != null)
            member.updateNickname(updateNickname);
        if (updateRegion != null)
            member.updateRegion(Region.fromRegionStr(updateRegion));
    }

    /**
     * 회원 탈퇴
     */
    @Override
    public void deleteMember(Member member, AppleDto.AppleCodeRequestDto appleCodeRequestDto) {
        // 애플 회원 탈퇴인 경우
        if(!(appleCodeRequestDto == null || appleCodeRequestDto.getAuthorizationCode().isEmpty())){
            if (!member.getUsername().equals(hashUtil.hash(appleCodeRequestDto.getUserIdentifier()))) {
                throw new AppleTokenValidationException();
            }
            // identityToken 서명 검증
            Claims claims = appleAuthUtil.verifyIdentityToken(appleCodeRequestDto.getIdentityToken());
            log.info("[AUTH] apple login verification : identityToken 검증 성공");
            // apple ID Server에 애플 토큰 요청
            AppleTokenResponseDto appleTokenResponseDto = appleAuthUtil.getAppleToken(appleCodeRequestDto.getAuthorizationCode());
            appleAuthUtil.revoke(appleTokenResponseDto.accessToken());
            log.info("[AUTH] revoke apple account, memberId = {}", member.getId());
        }
        List<Post> posts = member.getPosts();
        List<Comment> comments = member.getComments();

        for (Post post : posts) {
            post.setWriter(null);
        }
        for (Comment comment : comments) {
            comment.setWriter(null);
        }

        postRepository.saveAll(posts);
        commentRepository.saveAll(comments);
        memberRepository.delete(member);
    }

    private void checkIfDuplicatedMember(String username) {
        memberRepository.findByUsername(username).ifPresent(member -> {
                    throw new MemberDuplicatedException();
        });
    }

}
