package com.server.youthtalktalk.service.member;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.Role;
import com.server.youthtalktalk.domain.policy.Region;
import com.server.youthtalktalk.dto.member.MemberUpdateDto;
import com.server.youthtalktalk.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    private final String USERNAME = "myUsername";
    private final String NICKNAME = "사용자닉네임";

//    @BeforeEach
//    public void init(){
//        Member member = Member.builder()
//                .username(USERNAME)
//                .nickname(NICKNAME)
//                .region(Region.SEOUL)
//                .role(Role.USER).build();
//        memberRepository.save(member);
//        clear();
//    }
//
    private void clear(){
        em.flush();
        em.clear();
    }

    @Test
    void 회원정보_수정_성공() throws Exception {
        // given
        Member member = Member.builder()
                .username(USERNAME)
                .nickname(NICKNAME)
                .region(Region.SEOUL)
                .role(Role.USER).build();
        memberRepository.save(member);
        clear();
        String updateNickname = "닉네임수정";
        String updateRegion = "부산";
        MemberUpdateDto memberUpdateDto = new MemberUpdateDto(updateNickname, updateRegion);

        // when
        memberService.updateMemberInfo(memberUpdateDto, member);

        // then
        assertThat(member.getNickname()).isEqualTo(updateNickname);
        assertThat(member.getRegion().getName()).isEqualTo(updateRegion);

    }
}