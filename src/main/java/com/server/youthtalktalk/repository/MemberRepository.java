package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByUsername(String username);
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
