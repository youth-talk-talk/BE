package com.server.youthtalktalk.domain.policy.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.QPolicy;
import com.server.youthtalktalk.domain.policy.entity.SortOption;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.QPolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.QSubRegion;
import com.server.youthtalktalk.domain.post.entity.QReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.server.youthtalktalk.domain.policy.entity.QPolicy.policy;
import static com.server.youthtalktalk.domain.policy.entity.condition.Earn.OTHER;
import static com.server.youthtalktalk.domain.policy.entity.condition.Earn.UNRESTRICTED;

@Repository
public class PolicyQueryRepositoryImpl implements PolicyQueryRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    /**
     * 조건 적용 정책 조회
     */
    @Override
    public Page<Policy> findByCondition(SearchConditionDto condition, Pageable pageable, SortOption sortOption) {
        QPolicy policy = QPolicy.policy;
        BooleanBuilder predicate = new BooleanBuilder();

        filterByKeyword(condition, policy, predicate); // 키워드
        predicate.and(eqInstitutionType(condition.institutionType())); // 운영기관
        predicate.and(eqCategories(condition.categories())); // 카테고리
        predicate.and(eqMarriage(condition.marriage())); // 결혼 요건
        predicate.and(isAgeInRange(condition.age())); // 나이
        predicate.and(isEarnInRange(condition.minEarn(), condition.maxEarn())); // 소득 요건
        predicate.and(inEducations(condition.educations())); // 학력
        predicate.and(inMajors(condition.majors())); // 전공요건
        predicate.and(inEmployments(condition.employments())); // 취업상태
        predicate.and(inSpecializations(condition.specializations())); // 특화분야
        predicate.and(eqSubRegion(condition.subRegionIds())); // 지역
        predicate.and(eqIsFinished(condition.isFinished())); // 마감여부
        predicate.and(eqApplyDue(condition.applyDue())); // 마감일

        // 데이터 조회 쿼리
        List<Policy> policies = queryFactory
                .selectFrom(policy)
                .where(predicate)
                .orderBy(sortOption.getOrderSpecifiers())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 조회 쿼리
        long total = Optional.ofNullable(
                queryFactory
                        .select(policy.count())
                        .from(policy)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(policies, pageable, total);
    }

    @Override
    public Page<Policy> findAll(Pageable pageable, SortOption sortOption) {
        List<Policy> policies = queryFactory
                .selectFrom(policy)
                .orderBy(sortOption.getOrderSpecifiers())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 쿼리
        long total = Optional.ofNullable(
                queryFactory
                        .select(policy.count())
                        .from(policy)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(policies, pageable, total);
    }

    private void filterByKeyword(SearchConditionDto condition, QPolicy policy, BooleanBuilder predicate) {
        String keyword = condition.keyword();
        if (keyword != null && !keyword.isBlank()) {
            BooleanBuilder keywordPredicate = new BooleanBuilder();
            keywordPredicate.or(Expressions.stringTemplate("replace({0}, ' ', '')", policy.title).containsIgnoreCase(
                    keyword));
            keywordPredicate.or(Expressions.stringTemplate("replace({0}, ' ', '')", policy.introduction).containsIgnoreCase(
                    keyword));
            predicate.and(keywordPredicate);
        }
    }

    private BooleanExpression eqInstitutionType(InstitutionType type) {
        return type != null ? policy.institutionType.eq(type) : null;
    }

    private BooleanBuilder eqCategories(List<Category> categories) {
        if (categories == null || categories.isEmpty()) return null;
        BooleanBuilder categoryPredicate = new BooleanBuilder();
        for (Category category : categories) {
            categoryPredicate.or(policy.category.eq(category));
        }
        return categoryPredicate;
    }

    private BooleanExpression eqMarriage(Marriage marriage) {
        return marriage != null ? policy.marriage.eq(marriage) : null;
    }

    private BooleanExpression isAgeInRange(Integer age) {
        if (age == null) return null;
        return policy.isLimitedAge.isFalse() // 나이 제한 없는 정책 항상 포함
                .or(policy.minAge.loe(age).and(policy.maxAge.goe(age)));
    }

    private BooleanBuilder isEarnInRange(Integer minEarn, Integer maxEarn) {
        if (minEarn == null && maxEarn == null) return null;

        // 소득 제한 없는 경우 항상 포함 (UNRESTRICTED, OTHER)
        BooleanExpression noLimitEarn = policy.earn.in(UNRESTRICTED, OTHER);
        BooleanBuilder basePredicate = new BooleanBuilder(noLimitEarn);

        BooleanBuilder earnPredicate = new BooleanBuilder();
        if (minEarn != null) {
            earnPredicate.and(policy.minEarn.loe(minEarn));
        }
        if (maxEarn != null) {
            earnPredicate.and(policy.maxEarn.goe(maxEarn));
        }
        basePredicate.or(earnPredicate);

        return basePredicate;
    }

    private BooleanExpression inEducations(List<Education> educations) {
        return (educations != null && !educations.isEmpty()) ? policy.education.any().in(educations) : null;
    }

    private BooleanExpression inMajors(List<Major> majors) {
        return (majors != null && !majors.isEmpty()) ? policy.major.any().in(majors) : null;
    }

    private BooleanExpression inEmployments(List<Employment> employments) {
        return (employments != null && !employments.isEmpty()) ? policy.employment.any().in(employments) : null;
    }

    private BooleanExpression inSpecializations(List<Specialization> specializations) {
        return (specializations != null && !specializations.isEmpty()) ? policy.specialization.any().in(specializations) : null;
    }

    private BooleanExpression eqSubRegion(List<Long> subRegionIds) {
        if (subRegionIds == null || subRegionIds.isEmpty()) return null;

        QPolicySubRegion policySubRegion = QPolicySubRegion.policySubRegion;
        QSubRegion subRegion = QSubRegion.subRegion;

        return JPAExpressions
                .selectOne()
                .from(policySubRegion)
                .join(policySubRegion.subRegion, subRegion)
                .where(
                        policySubRegion.policy.policyId.eq(policy.policyId),
                        subRegion.id.in(subRegionIds)
                ).exists();
    }

    private BooleanExpression eqIsFinished(Boolean isFinished) {
        if (isFinished == null) return null;
        LocalDate today = LocalDate.now();
        return isFinished
                ? policy.applyDue.isNotNull().and(policy.applyDue.lt(today))
                : policy.applyDue.isNull().or(policy.applyDue.goe(today));
    }

    private BooleanExpression eqApplyDue(LocalDate applyDue) {
        return (applyDue != null) ? policy.applyDue.eq(applyDue) : null;
    }

    /**
     * 후기글이 많은 정책 top5 조회
     */
    public List<Policy> findTop5OrderByReviewCount() {
        QPolicy policy = QPolicy.policy;
        QReview review = QReview.review;

        // 정책의 리뷰 중에서 view >= 5인 리뷰의 수
        NumberExpression<Long> reviewCount = new CaseBuilder()
                .when(review.view.goe(5)).then(1L)
                .otherwise(0L)
                .sum();

        return queryFactory
                .select(policy)
                .from(policy)
                .innerJoin(policy.reviews, review)
                .groupBy(policy.policyId)
                .having(reviewCount.goe(2))
                .orderBy(reviewCount.desc(), policy.policyNum.desc())
                .limit(5)
                .fetch();
    }
}