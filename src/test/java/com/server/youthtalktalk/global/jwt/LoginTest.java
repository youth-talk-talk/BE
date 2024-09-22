package com.server.youthtalktalk.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERNAME = "username";
    private static String KEY_SOCIAL_TYPE = "socialType";
    private static String USERNAME = "777777";
    private static String SOCIAL_TYPE = "kakao";
    private static String LOGIN_URL = "/login";

    private void clear(){
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

    private Map<String, String> getUsernameMap(String username, String socialType){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_SOCIAL_TYPE, socialType);
        return map;
    }

    private ResultActions perform(String url, Map<String, String> requestBodyMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBodyMap)));

    }

    /**
     * 로그인 성공 - 200, 헤더에 token 반환
     * 로그인 실패 (db에 일치하는 회원정보 없음) - 403, 메시지 반환
     * 로그인 주소 틀림 - 403, 메시지 반환
     */

    @Test
    void 로그인_성공() throws Exception {
        // given
        Map<String, String> map = getUsernameMap(USERNAME, SOCIAL_TYPE);

        // when, then
        perform(LOGIN_URL, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void 로그인_실패_회원이_아님() throws Exception {
        // given
        Map<String, String> map = getUsernameMap(USERNAME+"111", SOCIAL_TYPE);

        // when, then
        perform(LOGIN_URL, map)
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void 로그인_주소가_틀리면_401() throws Exception {
        // given
        Map<String, String> map = getUsernameMap(USERNAME, SOCIAL_TYPE);

        // when, then
        perform(LOGIN_URL+"else", map)
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

}
