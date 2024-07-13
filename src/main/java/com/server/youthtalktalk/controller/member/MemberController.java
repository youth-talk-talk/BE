package com.server.youthtalktalk.controller.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.member.MemberInfoDto;
import com.server.youthtalktalk.dto.member.MemberUpdateDto;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/members/me")
    public BaseResponse<MemberInfoDto> getMemberInfo() {
        Member member = memberService.getCurrentMember();
        MemberInfoDto memberInfoDto = new MemberInfoDto(member.getId(), member.getNickname(), member.getRegion().getName());
        return new BaseResponse<>(memberInfoDto, SUCCESS);
    }

    @PatchMapping("/members/me")
    public BaseResponse<MemberInfoDto> updateMemberInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        Member member = memberService.getCurrentMember();
        memberService.updateMemberInfo(memberUpdateDto, member);
        MemberInfoDto updatedMemberInfo = new MemberInfoDto(member.getId(), member.getNickname(), member.getRegion().getName());
        return new BaseResponse<>(updatedMemberInfo, SUCCESS);
    }

    @DeleteMapping("/members/me")
    public BaseResponse<String> deleteMember() {
        Member member = memberService.getCurrentMember();
        memberService.deleteMember(member);
        return new BaseResponse<>(SUCCESS);
    }

}
