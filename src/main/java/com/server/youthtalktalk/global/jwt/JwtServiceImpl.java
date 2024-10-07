package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.global.response.exception.member.MemberNotFoundException;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;

    /**
     * access token 생성
     */
    @Override
    public String createAccessToken(String username) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(USERNAME_CLAIM, username)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * refresh token 생성
     */
    @Override
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * refresh token 갱신
     */
    @Override
    @Transactional
    public void updateRefreshToken(String username, String refreshToken) {
        memberRepository.findByUsername(username).ifPresentOrElse(
                member -> member.updateRefreshToken(refreshToken),
                MemberNotFoundException::new
        );
    }

    /**
     * refresh token 제거
     */
    @Override
    public void destroyRefreshToken(String username) {
        memberRepository.findByUsername(username).ifPresentOrElse(
                member -> {
                    member.destroyRefreshToken();
                    memberRepository.save(member);
                },
                () -> {
                    throw new MemberNotFoundException();
                }
        );
    }

    /**
     * access token 헤더로 전송
     */
    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * access token, refresh token 헤더로 전송
     */
    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        response.setHeader(refreshHeader, refreshToken);
    }

    /**
     * 헤더에서 access token 추출
     */
    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""))
                .or(() -> Optional.ofNullable(request.getCookies()) // 관리자 페이지 접근 시 쿠키로 토큰 인증
                        .flatMap(cookies -> Arrays.stream(cookies)
                                .filter(cookie -> "Authorization".equals(cookie.getName()))
                                .map(Cookie::getValue)
                                .findFirst()));
    }

    /**
     * 헤더에서 refresh token 추출
     */
    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * access token에서 username 추출
     * access token이 유효하면 username 반환, 유효하지 않으면 Optional.empty 반환
     */
    @Override
    public Optional<String> extractUsername(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(USERNAME_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * 토튼 유효성 검사
     */
    @Override
    public void isTokenValid(String token) throws JWTVerificationException {
        log.info("토큰 유효성 검사 시작");
        JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
        log.info("토큰 유효성 검사 완료");
    }

}
