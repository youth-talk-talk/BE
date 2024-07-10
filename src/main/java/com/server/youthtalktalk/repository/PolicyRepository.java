package com.server.youthtalktalk.repository;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy,String> {

    /**
     * 모든 정책 조회 (최신순)
     */
    Page<Policy> findAll(Pageable pageable);


    /**
     * top5 정책 조회 (조회수순)
     */
    List<Policy> findTop5ByOrderByViewDesc();

    /**
     * 카테고리별 정책 조회 (최신순) - 중복 가능
     */
    Page<Policy> findByCategoryIn(List<Category> categories, Pageable pageable);
}
