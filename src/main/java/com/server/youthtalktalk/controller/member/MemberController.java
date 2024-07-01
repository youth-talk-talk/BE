package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;
import com.server.youthtalktalk.dto.member.apple.AppleDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.member.MemberService;
import com.server.youthtalktalk.service.member.MemberServiceImpl;
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
     * 소셜 로그인한 사용자가 회원인지 판별
     * 회원이면 MEMBER, 회원가입 하지 않은 사용자면 GUEST
     */

    @PostMapping("/api/member/status")
    public String checkIfJoined(@RequestBody CheckIfJoinedDto checkIfJoinedDto) throws Exception {
        return memberService.checkIfJoined(checkIfJoinedDto);
    }

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
