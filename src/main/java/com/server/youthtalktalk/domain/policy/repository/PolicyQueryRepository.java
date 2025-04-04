package com.server.youthtalktalk.domain.policy.repository;

import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface PolicyQueryRepository {

    /**
     * 조건 적용 정책 조회
     */
    Page<Policy> findByCondition(SearchConditionDto condition, Pageable pageable);

}
