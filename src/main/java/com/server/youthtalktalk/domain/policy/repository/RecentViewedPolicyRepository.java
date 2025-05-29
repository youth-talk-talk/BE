package com.server.youthtalktalk.domain.policy.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.RecentViewedPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecentViewedPolicyRepository extends JpaRepository<RecentViewedPolicy, Long> {
    List<RecentViewedPolicy> findAllByMemberOrderByUpdatedAtDesc(Member member);
    void deleteAllByMember(Member member);
    Optional<RecentViewedPolicy> findByMemberAndPolicy(Member member, Policy policy);
    int countAllByMember(Member member);
    Optional<RecentViewedPolicy> findFirstByMemberOrderByUpdatedAt(Member member);
}
