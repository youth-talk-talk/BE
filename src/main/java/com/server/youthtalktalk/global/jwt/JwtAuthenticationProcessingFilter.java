package com.server.youthtalktalk.global.jwt;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.repository.MemberRepository;
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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    /**
     * JWT 인증 필터 - "/login" 이외의 요청을 처리
     *
     * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
     * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
     *
     * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken 재발급 X
     * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, MemberUnauthorizedException 발생
     * 3. RefreshToken이 있는 경우 -> AccessToken이 만료되어 RefreshToken을 함께 보낸 경우.
     *                              DB의 RefreshToken과 비교하여 일치하면
     *                              AccessToken과 RefreshToken 모두 재발급(RTR 방식), 인증은 실패 처리
     */

    private static final String NO_CHECK_URL = "/login";

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르다면) null을 반환
        // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료되어 요청한 경우밖에 없다.
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        // 3번 케이스 - refresh token이 DB의 refresh token과 일치하는지 판단 후,
        // 일치한다면 access token을 재발급한다.
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
        }

        // 1,2번 케이스 - RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증 처리
        // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    // refresh token으로 회원 정보 찾아서 access token, refresh token 재발급
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken).ifPresent(
                member -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(member);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(member.getUsername()),
                            reIssuedRefreshToken);
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
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .flatMap(jwtService::extractUsername)
                .flatMap(memberRepository::findByUsername)
                .ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 허가 처리
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */

    private void saveAuthentication(Member member) {
        UserDetails userDetailsUser = User.builder()
                .username(member.getUsername())
                .password("password")
                .roles(member.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, "password",
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
