package com.server.youthtalktalk.controller;

import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;
import com.server.youthtalktalk.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 소셜 로그인한 사용자가 회원인지 판별
     * 회원이면 MEMBER, 회원가입 하지 않은 사용자면 GUEST
     */

    @PostMapping("/api/member/status")
    public String checkIfJoined(@RequestBody CheckIfJoinedDto checkIfJoinedDto) throws Exception {
        return memberService.checkIfJoined(checkIfJoinedDto);
    }

}
