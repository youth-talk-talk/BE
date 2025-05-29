package com.server.youthtalktalk.domain.policy.entity;

import static com.querydsl.core.types.Order.*;
import static com.server.youthtalktalk.domain.policy.entity.QPolicy.policy;

import com.querydsl.core.types.OrderSpecifier;

public enum SortOption {
    RECENT(new OrderSpecifier[]{
            new OrderSpecifier<>(DESC, policy.policyNum)
    }),
    POPULAR(new OrderSpecifier[]{
            new OrderSpecifier<>(DESC, policy.view), // 1순위 - 조회수 높은 순
            new OrderSpecifier<>(DESC, policy.policyNum) // 2순위 - 최신순
    });

    private final OrderSpecifier<?>[] orderSpecifiers;

    SortOption(OrderSpecifier<?>[] orderSpecifiers) {
        this.orderSpecifiers = orderSpecifiers;
    }

    public OrderSpecifier<?>[] getOrderSpecifiers() {
        return orderSpecifiers;
    }
}
