package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.dto.member.apple.AppleTokenResponseDto;
import com.server.youthtalktalk.global.util.AppleAuthUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AppleAuthUtilTest {
    @Autowired
    private AppleAuthUtil appleAuthUtil;

    @Test
    @DisplayName("client_secret 생성 테스트")
    void getClientSecretTest() {
        String clientSecret = appleAuthUtil.createClientSecret();
        System.out.println("client_secret = " + clientSecret);
    }

//    @Test
//    @DisplayName("애플 토큰 요청 테스트")
//    void getAppleTokenTest(){
//        String authorizationCode = "";
//        AppleTokenResponseDto appleTokenResponseDto = appleAuthUtil.getAppleToken(authorizationCode);
//    }
//
//    @Test
//    @DisplayName("verifyIdToken 테스트")
//    void verifyIdTokenTest(){
//        String identityToken ="";
//
//        appleAuthUtil.verifyIdentityToken(identityToken);
//    }
}
