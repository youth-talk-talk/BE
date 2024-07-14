package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy,String> {


    /**
     * top5 정책 조회 (조회수순)
     */
    List<Policy> findTop5ByOrderByViewDesc();

    /**
     * 카테고리별 정책 조회 (최신순) - 카테고리 중복 선택 가능
     */
    Page<Policy> findByCategoryIn(List<Category> categories, Pageable pageable);


    /**
     * 특정 정책 조회
     */
    Optional<Policy> findById(String policyId);
}
