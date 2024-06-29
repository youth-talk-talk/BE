package com.server.youthtalktalk.global.login;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.global.response.error.member.MemberNotFoundException;
import com.server.youthtalktalk.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *    파라미터로 username으로 DB에서 일치하는 Member를 찾고,
 *    해당 회원의 username, email, Role을 담아 UserDetails의 User 객체를 생성한다.
 */

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
        return User.builder()
                .username(member.getUsername())
                .roles(member.getRole().name())
                .build();
    }

}
