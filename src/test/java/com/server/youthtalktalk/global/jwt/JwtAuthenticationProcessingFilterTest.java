package com.server.youthtalktalk.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.global.util.HashUtil;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtAuthenticationProcessingFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    HashUtil hashUtil;

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
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";

    private static String KEY_SOCIAL_TYPE = "socialType";
    private static String KEY_SOCIAL_ID = "socialId";
    private static String SOCIAL_TYPE = "kakao";
    private static String SOCIAL_ID = "77777";

    private ObjectMapper objectMapper = new ObjectMapper();

    public void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void init(){
        memberRepository.save(Member.builder()
                .username(hashUtil.hash(SOCIAL_ID))
                .role(Role.USER)
                .build());
        clear();
    }

    private Map<String, String> createRequestBodyMap(String socialType, String socialId){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_SOCIAL_TYPE, socialType);
        map.put(KEY_SOCIAL_ID, socialId);
        return map;
    }

    private Map<String, String> getAccessAndRefreshToken() throws Exception {
        Map<String, String> map = createRequestBodyMap(SOCIAL_TYPE, SOCIAL_ID);

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

//     1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken 재발급 X
//     2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리
//     3. RefreshToken이 있는 경우 -> AccessToken이 만료되어 RefreshToken을 함께 보낸 경우.
//            *                    DB의 RefreshToken과 비교하여 일치하면
//                                 AccessToken과 RefreshToken 모두 재발급(RTR 방식), 인증은 실패 처리

    /**
     * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공, RefreshToken 재발급 X
     */
    @Test
    void access_token만_보내서_인증() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);
        System.out.println("Received accessToken = " + accessToken);

        // when, then
        MvcResult result = mockMvc.perform(get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
        assertThat(responseRefreshToken).isNull();

    }

    /**
     * 2-1. RefreshToken이 없고, AccessToken이 없는 경우 -> 인증 실패
     */
    @Test
    void access_refresh_모두_존재_X() throws Exception {
        // when, then
        mockMvc.perform(
                        get("/"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }

    /**
     * 2-2. RefreshToken이 없고, AccessToken이 유효하지 않은 경우 -> 인증 실패
     */
    @Test
    void 유효하지않은_access_token만_보내서_인증X() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken = accessAndRefreshToken.get(accessHeader);

        // when, then
        mockMvc.perform(
                get("/")
                        .header(accessHeader,accessToken+"1"))
                .andExpectAll(status().isUnauthorized());
    }

    /**
     * RefreshToken은 유효하고, AccessToken은 존재하지 않음 -> AccessToken 재발급
     */
    @Test
    void 유효한_refresh_token만_보내서_access_token_재발급() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        MvcResult result = mockMvc.perform(
                get("/")
                        .header(refreshHeader, BEARER + refreshToken))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    /**
     * RefreshToken은 유효하지 않고, AccessToken은 존재하지 않음 -> 인증 실패
     */
    @Test
    void 유효하지않은_refresh_token만_보냄() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        mockMvc.perform(get("/")
                        .header(refreshHeader, BEARER + refreshToken+"1")) // 유효하지 않은 refresh token
                .andExpect(status().isUnauthorized());
    }

    /**
     * RefreshToken, AccessToken 모두 유효함 -> 모두 재발급
     */
    @Test
    void both_token_유효하면_모두_재발급() throws Exception {
        // given
        Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= accessAndRefreshToken.get(accessHeader);
        String refreshToken= accessAndRefreshToken.get(refreshHeader);

        // when, then
        MvcResult result = mockMvc.perform(
                get("/")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String accessTokenSubject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();
        String refreshTokenSubject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseRefreshToken).getSubject();

        assertThat(accessTokenSubject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(refreshTokenSubject).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

}