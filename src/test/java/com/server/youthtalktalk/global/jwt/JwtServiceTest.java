package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.global.response.error.member.MemberNotFoundException;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private String email = "example@email.com";

    @BeforeEach
    public void init(){
        Member member = Member.builder().email(email).nickname("nickname1").role(Role.MEMBER).build();
        memberRepository.save(member);
        clear();
    }

    private void clear(){
        em.flush();
        em.clear();
    }

    private DecodedJWT getVerify(String token) {
        return JWT.require(HMAC512(secret)).build().verify(token);
    }

    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);

        return httpServletRequest;
    }

    @Test
    void access_token_발급() throws Exception {
        // given, when
        String accessToken = jwtService.createAccessToken(email);
        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findEmail = verify.getClaim(EMAIL_CLAIM).asString();

        // then
        assertThat(findEmail).isEqualTo(email);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    void refresh_token_발급() throws Exception {
        // given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);

        String subject = verify.getSubject();
        String findEmail = verify.getClaim(EMAIL_CLAIM).asString();

        // then
        assertThat(findEmail).isNull();
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    void refresh_token_갱신() throws Exception {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(email, refreshToken);
        clear();
        Thread.sleep(3000);

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(email, reIssuedRefreshToken);
        clear();

        // then
        assertThat(memberRepository.findByRefreshToken(refreshToken).isEmpty()).isTrue();
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).isPresent()).isTrue();
    }

    @Test
    void refresh_token_제거() throws Exception {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(email, refreshToken);
        clear();

        // when
        jwtService.destroyRefreshToken(email);

        // then
        assertThat(memberRepository.findByRefreshToken(refreshToken).isEmpty()).isTrue();
        assertThat(memberRepository.findByEmail(email).get().getRefreshToken()).isNull();
    }

    @Test
    void access_token_헤더에_전송() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(email);

        // when
        jwtService.sendAccessToken(mockHttpServletResponse, accessToken);

        // then
        String receivedAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        assertThat(receivedAccessToken).isEqualTo(accessToken);
    }

    @Test
    void both_token_헤더에_전송() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        // when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String resultAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String resultRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        assertThat(resultAccessToken).isEqualTo(accessToken);
        assertThat(resultRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    void access_token_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(request).get();

        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(EMAIL_CLAIM).asString()).isEqualTo(email);
    }

    @Test
    void refresh_token_추출() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        // when
        String extractRefreshToken = jwtService.extractRefreshToken(request).get();

        // then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }
}