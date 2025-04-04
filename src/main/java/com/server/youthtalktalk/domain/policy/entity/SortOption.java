package com.server.youthtalktalk.domain.policy.entity;

import static com.querydsl.core.types.Order.*;
import static com.server.youthtalktalk.domain.policy.entity.QPolicy.policy;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

public enum SortOption {
    RECENT(policy.policyId, DESC),
    POPULAR(policy.view, DESC);

    private final OrderSpecifier<?> orderSpecifier;

    <T extends Comparable<?>> SortOption(ComparableExpressionBase<T> sortKey, Order order) {
        this.orderSpecifier = new OrderSpecifier<>(order, sortKey);
    }

    public OrderSpecifier<?> getOrderSpecifier() {
        return orderSpecifier;
    }
}
