package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class JwtAuthenticationProcessingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String LOGIN_URL = "/login";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";

    private static String KEY_USERNAME = "username";
    private static String USERNAME = "kakao12345678";

    private ObjectMapper objectMapper = new ObjectMapper();
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        memberRepository.save(Member.builder()
                .username(USERNAME)
                .role(Role.USER)
                .build());
        clear();
    }

    private Map<String, String> createRequestBodyMap(String username){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        return map;
    }

    private Map<String, String> getAccessAndRefreshToken() throws Exception {
        Map<String, String> map = createRequestBodyMap(USERNAME);

        System.out.println("로그인 요청 시작");
        MvcResult result = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader, accessToken);
        tokenMap.put(refreshHeader, refreshToken);

        System.out.println("tokenMap 반환");

        return tokenMap;
    }

    /**
     * access token, refresh token 모두 존재하지 않음
     */
    @Test
    void access_refresh_모두_존재_X() throws Exception {
        // when, then
        mockMvc.perform(
                get(LOGIN_URL+"else"))
                .andExpect(status().isForbidden());
    }

    /**
     * access token은 유효하고, refresh token은 존재하지 않음
     */
    @Test
    void access_token만_보내서_인증() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        System.out.println("Received accessToken = " + accessToken);

        // when, then
        mockMvc.perform(
                get(LOGIN_URL+"else")
                        .header(accessHeader,BEARER+ accessToken))
                .andExpect(status().isNotFound());

    }

    /**
     * access token은 유효하지 않고, refresh token은 존재하지 않음
     */
    @Test
    void 유효하지않은_access_token만_보내서_인증X() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        // when, then
        mockMvc.perform(
                get(LOGIN_URL+"else")
                        .header(accessHeader,accessToken+"1"))
                .andExpectAll(status().isForbidden());
    }

    /**
     * access token은 존재하지 않고, refresh token은 유효함
     */
    @Test
    void 유효한_refresh_token만_보내서_access_token_재발급() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        MvcResult result = mockMvc.perform(
                get(LOGIN_URL + "else")
                        .header(refreshHeader, BEARER + refreshToken))//login이 아닌 다른 임의의 주소
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    /**
     * access token은 존재하지 않고, refresh token은 유효하지 않음
     */
    @Test
    void 유효하지않은_refresh_token만_보냄() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        mockMvc.perform(
                get(LOGIN_URL + "else")
                        .header(refreshHeader, refreshToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get(LOGIN_URL + "else")
                        .header(refreshHeader, BEARER + refreshToken+"1"))
                .andExpect(status().isForbidden());
    }

    /**
     * access token과 refresh token 모두 유효함
     */
    @Test
    void both_token_모두유효하면_access_token_재발급() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        MvcResult result = mockMvc.perform(
                get(LOGIN_URL + "else")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();
    }
}