package com.server.youthtalktalk.global.login;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.global.util.HashUtil;
import com.server.youthtalktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 *    파라미터로 username으로 DB에서 일치하는 Member를 찾고,
 *    해당 회원의 username과 Role을 담아 UserDetails의 User 객체를 생성한다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final HashUtil hashUtil;

    /**
     * 파라미터 socialId를 해싱 처리한 후 db에서 일치하는 사용자를 조회한다.
     * 비밀번호는 인증 절차에 필요하지 않으므로 난수로 만든 다음 UserDetail 객체로 넘긴다.
     */

    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        log.info("loadUserByUsername 진입");
        log.info("socialId: {}", socialId);
        String username = hashUtil.hash(socialId);
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("로그인에 실패했습니다."));

        String randomPassword = UUID.randomUUID().toString();

        return User.builder()
                .username(member.getUsername())
                .password(randomPassword)
                .roles(member.getRole().name())
                .build();
    }

}
