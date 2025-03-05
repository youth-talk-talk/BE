package com.server.youthtalktalk.domain.policy.repository.region;

import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubRegionRepository extends JpaRepository<SubRegion, Long> {
    List<SubRegion> findAllByRegionAndCodeIn(Region region, List<String> codes);
    Optional<SubRegion> findByCode(String code);
}
