package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.member.MemberInfoDto;
import com.server.youthtalktalk.dto.member.MemberUpdateDto;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.global.response.exception.member.MemberAccessDeniedException;
import com.server.youthtalktalk.global.response.exception.member.MemberDuplicatedException;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;
    private final HttpServletResponse httpServletResponse;
    private final AppleAuthUtil appleAuthUtil;

    /**
     * 회원 가입
     */
    @Override
    public Long signUp(SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.getUsername();
        String nickname = signUpRequestDto.getNickname();
        Region region = Region.fromRegionStr(signUpRequestDto.getRegion());

        checkIfDuplicatedMember(signUpRequestDto.getUsername()); // 중복 회원 검증

        if (username.startsWith("apple")){
            String idToken = signUpRequestDto.getIdToken();
            appleAuthUtil.verifyIdentityToken(idToken); // 애플 토큰 검증
        }

        Member member = Member.builder().username(username).nickname(nickname).region(region).role(Role.USER).build();
        memberRepository.save(member);

        String accessToken = jwtService.createAccessToken(username);
        jwtService.sendAccessToken(httpServletResponse, accessToken);
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
     * 내 정보 수정
     */
    @Override
    public MemberInfoDto updateMemberInfo(MemberUpdateDto memberUpdateDto) {
        Member member = this.getCurrentMember();
        String updateNickname = memberUpdateDto.nickname();
        String updateRegion = memberUpdateDto.region();
        if (updateNickname != null)
            member.updateNickname(updateNickname);
        if (updateRegion != null)
            member.updateRegion(Region.fromRegionStr(updateRegion));
        return new MemberInfoDto(member.getId(), member.getNickname(), member.getRegion().getName());
    }

    private void checkIfDuplicatedMember(String username) {
        Optional<Member> findMember = memberRepository.findByUsername(username);
        if (findMember.isPresent()) {
            throw new MemberDuplicatedException();
        }
    }

}
