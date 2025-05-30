package com.server.youthtalktalk.domain.member.repository;

import com.server.youthtalktalk.domain.member.entity.Block;
import com.server.youthtalktalk.domain.member.entity.Member;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByMemberAndBlockedMember(Member member, Member blockedMember);

    boolean existsByMemberAndBlockedMember(Member member, Member blockedMember);

    @Query("SELECT b.blockedMember FROM Block b WHERE b.member = :member")
    List<Member> findBlockedMembersByBlocker(@Param("member") Member member);

}
