package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.member.MemberInfoDto;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.server.youthtalktalk.global.response.BaseResponseCode.SUCCESS;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signUp")
    public BaseResponse<Long> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Long memberId = memberService.signUp(signUpRequestDto);
        return new BaseResponse<>(memberId, SUCCESS);
    }

    @GetMapping("/me")
    public BaseResponse<MemberInfoDto> getMyInfo() {
        Member me = memberService.getCurrentMember();
        MemberInfoDto memberInfoDto = new MemberInfoDto(me.getNickname(), me.getEmail(), me.getRegion().getName());
        return new BaseResponse<>(memberInfoDto, SUCCESS);
    }
}
