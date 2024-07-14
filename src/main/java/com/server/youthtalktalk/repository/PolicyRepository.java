package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy,String> {


    /**
     * top5 정책 조회 (조회수순)
     */
    @Query(value = "SELECT * FROM policy p WHERE p.region = :region OR p.region = 'ALL' ORDER BY p.view DESC LIMIT 5", nativeQuery = true)
    List<Policy> findTop5ByRegionOrderByViewsDesc(@Param("region") Region region);

    /**
     * 카테고리별 정책 조회 (최신순) - 카테고리 중복 선택 가능
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'ALL') AND p.category IN :categories ORDER BY p.view DESC")
    Page<Policy> find5ByRegionAndCategory(@Param("region") Region region, @Param("categories") List<Category> categories, Pageable pageable);


    /**
     * 특정 정책 조회
     */
    Optional<Policy> findById(String policyId);
}
