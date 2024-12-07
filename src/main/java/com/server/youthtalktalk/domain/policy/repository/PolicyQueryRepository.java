package com.server.youthtalktalk.domain.policy.repository;

import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface PolicyQueryRepository {

    /**
     * 조건 적용 정책 조회
     */
    Page<Policy> findByCondition(Region region, List<Category> categories, Integer age, List<String> employmentCodes, Boolean isFinished, String keyword, Pageable pageable);

}
