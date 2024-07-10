package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;

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
    private static final String USERNAME_CLAIM = "username";
    private static final String BEARER = "Bearer ";

    private final String username = "myUsername";

    @BeforeEach
    public void init(){
        Member member = Member.builder().username(username).role(Role.USER).build();
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
        String accessToken = jwtService.createAccessToken(username);
        DecodedJWT verify = getVerify(accessToken);

        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(findUsername).isEqualTo(username);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    void refresh_token_발급() throws Exception {
        // given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);

        String subject = verify.getSubject();
        String findUsername = verify.getClaim(USERNAME_CLAIM).asString();

        // then
        assertThat(findUsername).isNull();
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    @Test
    void refresh_token_갱신() throws Exception {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();
        Thread.sleep(3000);

        // when
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, reIssuedRefreshToken);
        clear();

        // then
        assertThat(memberRepository.findByRefreshToken(refreshToken).isEmpty()).isTrue();
        assertThat(memberRepository.findByRefreshToken(reIssuedRefreshToken).isPresent()).isTrue();
    }

    @Test
    void refresh_token_제거() throws Exception {
        // given
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(username, refreshToken);
        clear();

        // when
        jwtService.destroyRefreshToken(username);

        // then
        assertThat(memberRepository.findByRefreshToken(refreshToken).isEmpty()).isTrue();
        assertThat(memberRepository.findByUsername(username).get().getRefreshToken()).isNull();
    }

    @Test
    void access_token_헤더에_전송() throws Exception {
        // given
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        String accessToken = jwtService.createAccessToken(username);

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
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        // when
        jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);

        // then
        String receivedAccessToken = mockHttpServletResponse.getHeader(accessHeader);
        String receivedRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
        assertThat(receivedAccessToken).isEqualTo(accessToken);
        assertThat(receivedRefreshToken).isEqualTo(refreshToken);
    }

    @Test
    void access_token_추출() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        //when
        String extractAccessToken = jwtService.extractAccessToken(request).get();

        //then
        assertThat(extractAccessToken).isEqualTo(accessToken);
        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
    }

    @Test
    void refresh_token_추출() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);

        // when
        String extractRefreshToken = jwtService.extractRefreshToken(request).get();

        // then
        assertThat(extractRefreshToken).isEqualTo(refreshToken);
        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }
    
    @Test
    void access_token에서_username_추출() throws Exception {
        // given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();
        HttpServletRequest request = setRequest(accessToken, refreshToken);
        String extractedAccessToken = jwtService.extractAccessToken(request).get();

        // when
        Optional<String> extractedUsername = jwtService.extractUsername(extractedAccessToken);

        // then
        assertThat(extractedUsername.isPresent()).isTrue();
        assertThat(extractedUsername.get()).isEqualTo(username);
    }

    @Test
    void 토큰_유효성_검사() throws Exception {
        //given
        String accessToken = jwtService.createAccessToken(username);
        String refreshToken = jwtService.createRefreshToken();

        //when, then
        assertThat(jwtService.isTokenValid(accessToken)).isTrue();
        assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
        assertThat(jwtService.isTokenValid(accessToken+"a")).isFalse();
        assertThat(jwtService.isTokenValid(accessToken+"a")).isFalse();

    }
}