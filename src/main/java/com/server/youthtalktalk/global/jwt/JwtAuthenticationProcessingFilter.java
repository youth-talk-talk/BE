package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.token.InvalidTokenException;
import com.server.youthtalktalk.repository.MemberRepository;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    /**
     * JWT 인증 필터 - "/login" 이외의 요청을 처리
     * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
     * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
     * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공, RefreshToken 재발급 X
     * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 (401)
     * 3. RefreshToken이 있는 경우 -> AccessToken이 만료되어 RefreshToken을 함께 보낸 경우이므로
     * 유효한 refresh token -> access, refresh 모두 재발급(RTR 방식)
     * 유효하지 않은 refresh token -> 인증 실패 (401)
     */

    private static final List<String> NO_CHECK_URL = Arrays.asList("/login", "/signUp");
    private static final String HEALTH_CHECK = "/actuator/health";

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, InvalidTokenException {

        if (NO_CHECK_URL.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().equals(HEALTH_CHECK)) {
            authenticationForHealthCheck(request);
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 RefreshToken 추출
        jwtService.extractRefreshToken(request).ifPresentOrElse(
                refreshToken -> { // refresh token이 있는 경우
                    checkRefreshToken(response, refreshToken);
                },
                () -> {
                    // refresh token이 없는 경우
                    // 1,2번 케이스 - RefreshToken이 없다면, AccessToken을 검사하고 인증 처리
                    // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터 진행 -> 인증 성공
                    // AccessToken이 없거나 유효하지 않다면, InvalidTokenException 발생 -> 인증 실패(401)
                    checkAccessTokenAndAuthentication(request, response, filterChain);
                }
        );
    }

    private void checkRefreshToken(HttpServletResponse response, String refreshToken) throws InvalidTokenException{
        log.info("refresh token 유효성 검증");
        try {
            jwtService.isTokenValid(refreshToken);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(BaseResponseCode.INVALID_REFRESH_TOKEN);
        }
        if (isRefreshTokenValidInDatabase(refreshToken)) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
        } else {
            throw new InvalidTokenException(BaseResponseCode.INVALID_REFRESH_TOKEN);
        }
    }

    // refresh token으로 회원 정보 찾아서 access token, refresh token 재발급
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        log.info("checkRefreshTokenAndReIssueAccessToken 진입");
        memberRepository.findByRefreshToken(refreshToken).ifPresent(
                member -> {
                    // 기존 refresh token 무효화
                    jwtService.destroyRefreshToken(member.getUsername());
                    // 새로운 refresh token, access token 발급 및 헤더로 전송
                    String reIssuedRefreshToken = reIssueRefreshToken(member);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(member.getUsername()),
                            reIssuedRefreshToken);
                    log.info("access, refresh 재발급 완료");
                }
        );
    }

    // refresh token 재발급 후 DB에 업데이트
    private String reIssueRefreshToken(Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberRepository.saveAndFlush(member);
        return reIssuedRefreshToken;
    }

    // access token 검사 및 인증 처리
    // request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
    // 유효한 토큰이면, 액세스 토큰에서 extractUsername으로 username을 추출한 후 findByUsername()로 Member 객체 반환
    // 그 유저 객체를 saveAuthentication()으로 인증 처리하여
    // 인증 허가 처리된 객체를 SecurityContextHolder에 담은 후 다음 필터로 넘김
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws InvalidTokenException{
        log.info("checkAccessTokenAndAuthentication 진입");
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(() -> new InvalidTokenException(BaseResponseCode.INVALID_ACCESS_TOKEN));
        try {
            jwtService.isTokenValid(accessToken);
            jwtService.extractUsername(accessToken)
                    .flatMap(memberRepository::findByUsername)
                    .ifPresentOrElse(
                            member -> {
                                saveAuthentication(member); // 인증 허가 처리
                                try {
                                    filterChain.doFilter(request, response);
                                } catch (IOException | ServletException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            () -> {
                                throw new InvalidTokenException(BaseResponseCode.INVALID_ACCESS_TOKEN);
                            }
                    );
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(BaseResponseCode.INVALID_ACCESS_TOKEN);
        }
    }

    /**
     * 인증 허가 처리
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */

    private void saveAuthentication(Member member) {
        String randomPassword = UUID.randomUUID().toString();

        UserDetails userDetails = User.builder()
                .username(member.getUsername())
                .password(randomPassword)
                .roles(member.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                        authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void authenticationForHealthCheck(HttpServletRequest request){
        String username = request.getHeader("username");

        Member member = memberRepository.findByUsername(username).get();
        if(member.getRole().equals(Role.ADMIN)){
            saveAuthentication(member);
        }
    }

    // 요청의 refresh token이 db에 저장된 refresh token과 일치하는지 검사
    private boolean isRefreshTokenValidInDatabase(String refreshToken){
        log.info("isRefreshTokenValidInDatabase 진입");
        boolean result = false;
        if (memberRepository.findByRefreshToken(refreshToken).isPresent()) {
            return true;
        }
        return result;
    }

}
