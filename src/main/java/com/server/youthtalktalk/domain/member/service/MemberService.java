package com.server.youthtalktalk.domain.member.service;

import com.server.youthtalktalk.domain.member.dto.MemberInfoDto;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.dto.MemberUpdateDto;
import com.server.youthtalktalk.domain.member.dto.SignUpRequestDto;
import com.server.youthtalktalk.domain.member.dto.apple.AppleDto;

public interface MemberService {
    Long signUp(SignUpRequestDto signUpRequestDto);
    Member getCurrentMember();
    void updateMemberInfo(MemberUpdateDto memberUpdateDto, Member member);
    void deleteMember(Member member, AppleDto.AppleCodeRequestDto appleCodeRequestDto);
    void blockMember(Member member, Long blockId);
    void unblockMember(Member member, Long unblockId);
    MemberInfoDto getMemberInfo(Member member);
}
