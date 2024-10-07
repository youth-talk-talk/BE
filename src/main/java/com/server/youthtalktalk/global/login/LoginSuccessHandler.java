package com.server.youthtalktalk.global.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.dto.member.LoginSuccessDto;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        // access token, refresh token 발급
        String username = extractUsername(authentication);
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        // refresh token 업데이트
        Member findMember = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
        findMember.updateRefreshToken(refreshToken);
        memberRepository.saveAndFlush(findMember);

        // 응답 헤더 및 바디 설정
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new BaseResponse<>(new LoginSuccessDto(findMember.getId()), SUCCESS)));

        // 관리자가 로그인했을 때만 access token 쿠키 발급
        if(findMember.getRole().equals(Role.ADMIN)) {
            Cookie accessCookie = new Cookie("Authorization", accessToken);
            accessCookie.setMaxAge(3600); // 1시간
            accessCookie.setPath("/");
            accessCookie.setSecure(false); // https 적용 여부
            response.addCookie(accessCookie);
        }

        log.info("로그인에 성공하였습니다. 회원 id={}", findMember.getId());
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
