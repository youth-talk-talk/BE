package com.server.youthtalktalk.domain.member.controller;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.dto.MemberInfoDto;
import com.server.youthtalktalk.domain.member.dto.MemberUpdateDto;
import com.server.youthtalktalk.domain.member.dto.SignUpRequestDto;
import com.server.youthtalktalk.domain.member.dto.apple.AppleDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 api
     */
    @PostMapping("/signUp")
    public BaseResponse<Long> signUp(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        Long memberId = memberService.signUp(signUpRequestDto);
        return new BaseResponse<>(memberId, SUCCESS);
    }

    /**
     * 회원정보 조회 api
     */
    @GetMapping("/members/me")
    public BaseResponse<MemberInfoDto> getMemberInfo() {
        Member member = memberService.getCurrentMember();
        MemberInfoDto memberInfoDto = new MemberInfoDto(member.getId(), member.getNickname(), member.getRegion().getName());
        return new BaseResponse<>(memberInfoDto, SUCCESS);
    }

    /**
     * 회원정보 수정 api
     */
    @PatchMapping("/members/me")
    public BaseResponse<MemberInfoDto> updateMemberInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto) {
        Member member = memberService.getCurrentMember();
        memberService.updateMemberInfo(memberUpdateDto, member);
        MemberInfoDto updatedMemberInfo = new MemberInfoDto(member.getId(), member.getNickname(), member.getRegion().getName());
        return new BaseResponse<>(updatedMemberInfo, SUCCESS_MEMBER_UPDATE);
    }

    /**
     * 회원탈퇴 api
     */
    @PostMapping("/members/me")
    public BaseResponse<String> deleteMember(@RequestBody(required = false) AppleDto.AppleCodeRequestDto appleCodeRequestDto) {
        Member member = memberService.getCurrentMember();
        memberService.deleteMember(member,appleCodeRequestDto);
        return new BaseResponse<>(SUCCESS_MEMBER_DELETE);
    }

}
