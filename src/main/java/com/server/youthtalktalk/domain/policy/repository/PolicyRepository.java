package com.server.youthtalktalk.domain.policy.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface PolicyRepository extends JpaRepository<Policy,String>, PolicyQueryRepository {

    /**
     * 전체 지역의 top20 정책 조회
     */
    @NonNull
    Page<Policy> findAll(@NonNull Pageable pageable);

    /**
     * 특정 지역의 top20 정책 조회
     */
    @Query("SELECT p FROM Policy p WHERE p.region = :region OR p.region = 'CENTER'")
    Page<Policy> findTop20ByRegion(@Param("region") Region region, Pageable pageable);

    /**
     * 지정된 기간 동안 생성된 정책 조회 (카테고리 필터링 X)
     */
    List<Policy> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Sort sort);

    /**
     * 지정된 기간 동안 생성된 정책 조회 (카테고리 필터링 O)
     */
    List<Policy> findByCreatedAtBetweenAndCategory(LocalDateTime from, LocalDateTime to, Category category, Sort sort);

    /**
     * 이름으로 정책 조회 (최신순)
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'CENTER') AND  (REPLACE(p.title, ' ', '') LIKE CONCAT('%', :title, '%')) ORDER BY p.policyNum DESC")
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
