package com.server.youthtalktalk.global.login;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String username = extractUsername(authentication); // 인증 정보에서 username 추출
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        memberRepository.findByUsername(username)
                .ifPresent(member -> {
                    member.updateRefreshToken(refreshToken);
                    memberRepository.saveAndFlush(member);
                    log.info("로그인에 성공하였습니다.");
                    log.info("회원 id={}, access token={}", member.getId(), accessToken);
                });

        super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
