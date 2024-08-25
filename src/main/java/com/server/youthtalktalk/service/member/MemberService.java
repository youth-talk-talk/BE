package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.member.MemberUpdateDto;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;
import com.server.youthtalktalk.dto.member.apple.AppleDto;

public interface MemberService {
    Long signUp(SignUpRequestDto signUpRequestDto);
    Member getCurrentMember();
    void updateMemberInfo(MemberUpdateDto memberUpdateDto, Member member);
    void deleteMember(Member member, AppleDto.AppleCodeRequestDto appleCodeRequestDto);
}
