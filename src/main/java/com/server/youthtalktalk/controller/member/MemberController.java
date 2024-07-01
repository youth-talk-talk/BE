package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.server.youthtalktalk.dto.member.apple.AppleDto.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;

    /**
     * 애플 로그인 처리
     * 로그인 과정에서 판별이 필요함
     */
    @PostMapping("/api/member/status/apple")
    public BaseResponse<String> appleLogin(@RequestBody AppleLoginRequestDto appleLoginRequestDto) {
        memberService.appleLogin(appleLoginRequestDto.getUserIdentifier(),appleLoginRequestDto.getIdentityToken(), appleLoginRequestDto.getAuthorizationCode());
        return new BaseResponse<>("", BaseResponseCode.SUCCESS);
    }
}
