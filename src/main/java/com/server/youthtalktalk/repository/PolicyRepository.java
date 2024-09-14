package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.member.Member;
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
public interface PolicyRepository extends JpaRepository<Policy,String>, PolicyQueryRepository {


    /**
     * top5 정책 조회 (조회수순)
     */
    @Query("SELECT p FROM Policy p WHERE p.region = :region OR p.region = 'ALL' ORDER BY p.view DESC")
    Page<Policy> findTop5ByRegionOrderByViewsDesc(@Param("region") Region region, Pageable pageable);

    /**
     * 카테고리별 정책 조회 (최신순) - 카테고리 중복 선택 가능
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'ALL') AND (p.category IN :categories) ORDER BY p.policyId DESC")
    Page<Policy> findByRegionAndCategory(@Param("region") Region region, @Param("categories") List<Category> categories, Pageable pageable);

    /**
     * 이름으로 정책 조회 (최신순)
     */
    @Query("SELECT p FROM Policy p WHERE (p.region = :region OR p.region = 'ALL') AND  (REPLACE(p.title, ' ', '') LIKE CONCAT('%', :title, '%')) ORDER BY p.policyId DESC")
    Page<Policy> findByRegionAndTitle(@Param("region") Region region, @Param("title") String title, Pageable pageable);


    /**
     * 특정 정책 조회
     */
    Optional<Policy> findById(String policyId);

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
}
