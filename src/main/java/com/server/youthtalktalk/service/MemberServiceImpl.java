package com.server.youthtalktalk.service;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.member.SocialType;
import com.server.youthtalktalk.dto.member.CheckIfJoinedDto;
import com.server.youthtalktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public String checkIfJoined(CheckIfJoinedDto checkIfJoinedDto) {
        String socialType = checkIfJoinedDto.getSocialType().toUpperCase();
        String socialId = checkIfJoinedDto.getSocialId();
        return memberRepository.findBySocialTypeAndSocialId(SocialType.valueOf(socialType), socialId)
                .map(member -> member.getRole().name()).orElseGet(() -> "회원정보 없음");
    }

}
