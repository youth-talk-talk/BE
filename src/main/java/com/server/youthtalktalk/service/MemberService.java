package com.server.youthtalktalk.service;

import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;

public interface MemberService {
    public String checkIfJoined(CheckIfJoinedDto checkIfJoinedDto); // 회원 판별
}
