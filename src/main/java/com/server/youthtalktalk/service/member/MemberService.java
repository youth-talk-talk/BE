package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;
import com.server.youthtalktalk.dto.member.apple.AppleDto.AppleLoginTestDto;

public interface MemberService {
    String checkIfJoined(CheckIfJoinedDto checkIfJoinedDto); // 회원 판별
    void appleLogin(String userIdentifier, String identityToken, String authorizationCode);
}
