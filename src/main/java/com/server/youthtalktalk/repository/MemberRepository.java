package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.member.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 소셜 타입과 식별값으로 회원 조회
     */
    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
