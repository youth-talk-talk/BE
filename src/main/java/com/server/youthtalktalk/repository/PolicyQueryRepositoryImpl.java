package com.server.youthtalktalk.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.policy.Policy;
import com.server.youthtalktalk.domain.policy.QPolicy;
import com.server.youthtalktalk.domain.policy.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PolicyQueryRepositoryImpl implements PolicyQueryRepository {

    @Autowired
    private JPAQueryFactory queryFactory;


    /**
     * 조건 적용 정책 조회
     */
    @Override
    public Page<Policy> findByCondition(Region region, List<Category> categories, Integer age, List<String> employmentCodes, Boolean isFinished, String keyword, Pageable pageable) {
        QPolicy policy = QPolicy.policy;

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(policy.region.eq(region).or(policy.region.eq(Region.ALL)));


        // 카테고리 필터
        if (categories != null && !categories.isEmpty()) {
            predicate.and(policy.category.in(categories));
        }

        // 연령 필터
        if (age != null) {
            predicate.and(policy.minAge.loe(age).and(policy.maxAge.goe(age)));
        }

        // 취업 상태 코드 필터
        if (employmentCodes != null && !employmentCodes.isEmpty()) {
            BooleanBuilder employmentCodePredicate = new BooleanBuilder();
            for (String code : employmentCodes) {
                employmentCodePredicate.or(policy.employmentCode.containsIgnoreCase(code));
            }
            predicate.and(employmentCodePredicate);
        }

        // 마감여부 필터
        if (isFinished != null) {
            if (isFinished) {
                predicate.and(policy.applyDue.isNotNull().and(policy.applyDue.lt(LocalDate.now())));
            } else {
                predicate.and(policy.applyDue.isNull().or(policy.applyDue.goe(LocalDate.now())));
            }
        }

        // 키워드 필터
        if (keyword != null && !keyword.isEmpty()) {
            predicate.and(policy.title.containsIgnoreCase(keyword)
                    .or(policy.introduction.containsIgnoreCase(keyword)));
        }

        // 정렬 (policyId 기준)
        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, policy.policyId);

        // 조건 적용
        List<Policy> policies = queryFactory.selectFrom(policy)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징을 위한 사이즈 체크
        long total = queryFactory.selectFrom(policy)
                .where(predicate)
                .fetch().size();

        return new PageImpl<>(policies, pageable, total);
    }
}