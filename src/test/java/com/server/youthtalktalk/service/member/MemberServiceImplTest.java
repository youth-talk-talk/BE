package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.dto.member.apple.AppleDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.server.youthtalktalk.dto.member.apple.AppleDto.*;

@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceImplTest {
    @Autowired
    private MemberServiceImpl memberService;

//    @Test
//    @DisplayName("로그인 테스트")
//    void appleLoginTest(){
//        String userIdentifier = "";
//        String identityToken = "";
//        String authorizationCode = "";
//        AppleLoginTestDto appleLoginTestDto = memberService.appleLogin(userIdentifier, identityToken, authorizationCode);
//    }
}
