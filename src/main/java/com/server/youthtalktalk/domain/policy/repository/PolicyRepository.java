package com.server.youthtalktalk.domain.policy.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PolicyRepository extends JpaRepository<Policy,String>, PolicyQueryRepository {

    /**
     * top20 정책 조회 (조회수순)
     * 여러 지역 선택 가능
     */
    @Query("SELECT p FROM Policy p WHERE p.region IN :regions OR p.region = 'ALL' ORDER BY p.view DESC")
    Page<Policy> findTop20ByRegionsOrderByViewsDesc(@Param("regions") List<Region> regions, Pageable pageable);

    /**
     * top20 정책 조회 (조회수순)
     * 모든 지역 선택
     */
    @Query("SELECT p FROM Policy p ORDER BY p.view DESC")
    Page<Policy> findTop20OrderByViewsDesc(Pageable pageable);

    /**
     * 카테고리별 정책 조회 (최신순) - 카테고리 중복 선택 가능
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'ALL') AND (p.category IN :categories) ORDER BY p.policyNum DESC")
    Page<Policy> findByRegionAndCategory(@Param("region") Region region, @Param("categories") List<Category> categories, Pageable pageable);


    /**
     * 오늘 포함 지난 7일의 정책 조회 (최신순)
     * 여러 지역 선택 가능
     */
    @Query("SELECT p FROM Policy p " +
            "WHERE (p.region IN :regions OR p.region = 'ALL') " +
            "AND p.category IN :categories " +
            "AND p.createdAt BETWEEN :from AND :to " +
            "ORDER BY p.createdAt DESC")
    Page<Policy> findRecentPoliciesByRegionAndCategory(@Param("regions") List<Region> regions,
                                                       @Param("categories") List<Category> categories,
                                                       @Param("from") LocalDateTime from,
                                                       @Param("to") LocalDateTime to,
                                                       Pageable pageable);
    /**
     * 오늘 포함 지난 7일의 정책 조회 (최신순)
     * 모든 지역 선택
     */
    @Query("SELECT p FROM Policy p " +
            "WHERE p.category IN :categories " +
            "AND p.createdAt BETWEEN :from AND :to " +
            "ORDER BY p.createdAt DESC")
    Page<Policy> findRecentPoliciesAndCategory(@Param("categories") List<Category> categories,
                                                       @Param("from") LocalDateTime from,
                                                       @Param("to") LocalDateTime to,
                                                       Pageable pageable);

    /**
     * 이름으로 정책 조회 (최신순)
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'ALL') AND  (REPLACE(p.title, ' ', '') LIKE CONCAT('%', :title, '%')) ORDER BY p.policyNum DESC")
    Page<Policy> findByRegionAndTitle(@Param("region") Region region, @Param("title") String title, Pageable pageable);

    /**
     * 특정 정책 조회
     */
    Optional<Policy> findByPolicyId(Long policyId);

    /**
     * 스크랩한 정책 조회(최신순)
     */
    @Query("select p from Policy p join Scrap s on s.itemId = p.policyId where s.member = :member order by s.id DESC ")
    Page<Policy> findAllByScrap(@Param("member") Member member, Pageable pageable);

    /**
     * 스크랩한 마감임박 정책 조회 (임박순, 최대 5개)
     */
    @Query("select p from Policy p join Scrap s on s.itemId = p.policyId where s.member = :member and (p.applyDue > CURRENT_DATE or p.applyDue is null) order by case when p.applyDue is null then 1 else 0 end, p.applyDue asc")

    Page<Policy> findTop5OrderByDeadlineAsc(Member member, Pageable pageable);

    Optional<Policy> findByPolicyNum(String policyNum);

    /**
     * policyId로 정책 존재 여부 검사
     */
    boolean existsByPolicyId(Long policyId);
}
