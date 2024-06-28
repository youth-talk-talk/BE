package com.server.youthtalktalk.service.member;

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
    void getClientSecret() {
        String clientSecret = appleAuthUtil.createClientSecret();
        System.out.println("client_secret = " + clientSecret);
    }
}
