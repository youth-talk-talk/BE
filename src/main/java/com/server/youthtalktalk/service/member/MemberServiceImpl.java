package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.global.response.exception.member.MemberDuplicatedException;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
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

    private void checkIfDuplicatedMember(String username) {
        Optional<Member> findMember = memberRepository.findByUsername(username);
        if (findMember.isPresent()) {
            throw new MemberDuplicatedException();
        }
    }

}
