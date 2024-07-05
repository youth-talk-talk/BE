package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.global.jwt.JwtService;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public BaseResponse<Long> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Long memberId = memberService.signUp(signUpRequestDto);
        return new BaseResponse<>(memberId, BaseResponseCode.SUCCESS);
    }
}
