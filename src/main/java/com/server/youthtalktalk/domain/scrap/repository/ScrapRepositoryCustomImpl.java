package com.server.youthtalktalk.domain.scrap.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import com.server.youthtalktalk.domain.policy.entity.QPolicy;
import com.server.youthtalktalk.domain.scrap.dto.PolicyScrapInfoDto;
import com.server.youthtalktalk.domain.scrap.entity.QScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScrapRepositoryCustomImpl implements ScrapRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QPolicy policy = QPolicy.policy;
    QScrap scrap = QScrap.scrap;

    @Override
    public List<PolicyScrapInfoDto> findRecentByDeadlineOrScrapDate() {
        List<PolicyScrapInfoDto> results = queryFactory
                .select(Projections.constructor(
                        PolicyScrapInfoDto.class,
                        policy.title,        // String policyTitle
                        policy.policyId,           // Long policyId
                        scrap.member,                   // member
                        policy.applyDue,
                        scrap.createdAt
                ))
                .from(policy)
                .join(scrap).on(policy.policyId.eq(scrap.itemId))
                .where(
                        scrap.itemType.eq(ItemType.POLICY),
                        // 마감일이 당일이거나 일주일 후
                        policy.applyDue.eq(LocalDate.now())
                        .or(policy.applyDue.eq(LocalDate.now().plusDays(7)))
                        // 또는 스크랩한 지 일주일이 지났을 때
                        .or(scrap.createdAt.before(LocalDateTime.now().minusDays(7)))

                )
                .fetch();

        return results;
    }
}
