package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.dto.member.MemberUpdateDto;
import com.server.youthtalktalk.dto.member.SignUpRequestDto;

public interface MemberService {
    Long signUp(SignUpRequestDto signUpRequestDto);
    Member getCurrentMember();
    void updateMemberInfo(MemberUpdateDto memberUpdateDto, String username);
}
