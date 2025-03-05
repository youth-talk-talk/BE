package com.server.youthtalktalk.domain.policy.repository.region;

import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicySubRegionRepository extends JpaRepository<PolicySubRegion, Long> {
    void deleteAllByPolicyIn(List<Policy> policy);
}
