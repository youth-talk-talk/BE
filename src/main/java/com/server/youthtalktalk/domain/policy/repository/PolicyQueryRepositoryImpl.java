package com.server.youthtalktalk.domain.policy.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.policy.dto.SearchConditionDto;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.QPolicy;
import com.server.youthtalktalk.domain.policy.entity.SubCategory;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.QPolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Repository
public class PolicyQueryRepositoryImpl implements PolicyQueryRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    /**
     * 조건 적용 정책 조회
     */
    @Override
    public Page<Policy> findByCondition(SearchConditionDto condition, Pageable pageable) {
        QPolicy policy = QPolicy.policy;
        QPolicySubRegion policySubRegion = QPolicySubRegion.policySubRegion;
        BooleanBuilder predicate = new BooleanBuilder();

        // 조건 결합
        filterByKeyword(condition, policy, predicate); // 키워드
        predicate.and(eqInstitutionType(condition.institutionType())); // 운영기관
        predicate.and(eqSubCategories(condition.subCategories())); // 카테고리
        predicate.and(eqSubRegion(condition.subRegions())); // 지역
        predicate.and(eqMarriage(condition.marriage())); // 결혼 요건
        predicate.and(gteMinAge(condition.age())).and(lteMaxAge(condition.age())); // 나이
        predicate.and(gteMinEarn(condition.minEarn())).and(lteMaxEarn(condition.maxEarn())); // 소득
        predicate.and(inEducations(condition.educations())); // 학력
        predicate.and(inMajors(condition.majors())); // 전공요건
        predicate.and(inEmployments(condition.employments())); // 취업상태
        predicate.and(inSpecializations(condition.specializations())); // 특화분야

        // 데이터 조회 쿼리
        List<Policy> policies = queryFactory
                .selectDistinct(policy)
                .from(policy)
                .leftJoin(policy.policySubRegions, policySubRegion).fetchJoin()
                .where(predicate)
                .orderBy(policy.policyId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        long totalCount = Optional.ofNullable(
                queryFactory
                        .select(policy.count())
                        .from(policy)
                        .where(predicate)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(policies, pageable, totalCount);
    }

    private void filterByKeyword(SearchConditionDto condition, QPolicy policy, BooleanBuilder predicate) {
        String keyword = condition.keyword();
        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword.replaceAll(" ", "");
            BooleanBuilder keywordPredicate = new BooleanBuilder();
            keywordPredicate.or(Expressions.stringTemplate("replace({0}, ' ', '')", policy.title).containsIgnoreCase(
                    keyword));
            keywordPredicate.or(Expressions.stringTemplate("replace({0}, ' ', '')", policy.introduction).containsIgnoreCase(
                    keyword));
            predicate.and(keywordPredicate);
        }
    }

    private BooleanExpression eqInstitutionType(InstitutionType type) {
        return type != null ? QPolicy.policy.institutionType.eq(type) : null;
    }

    private BooleanBuilder eqSubCategories(List<SubCategory> subCategories) {
        if (subCategories == null || subCategories.isEmpty()) return null;
        BooleanBuilder subCategoryPredicate = new BooleanBuilder();
        for (SubCategory subCategory : subCategories) {
            subCategoryPredicate.or(QPolicy.policy.subCategory.eq(subCategory));
        }
        return subCategoryPredicate;
    }

    private BooleanExpression eqSubRegion(List<SubRegion> regions) {
        if (regions == null || regions.isEmpty()) return null;
        return QPolicySubRegion.policySubRegion.subRegion.in(regions);
    }

    private BooleanExpression eqMarriage(Marriage marriage) {
        return marriage != null ? QPolicy.policy.marriage.eq(marriage) : null;
    }

    private BooleanExpression gteMinAge(Integer age) {
        return age != null ? QPolicy.policy.minAge.loe(age) : null;
    }

    private BooleanExpression lteMaxAge(Integer age) {
        return age != null ? QPolicy.policy.maxAge.goe(age) : null;
    }

    private BooleanExpression gteMinEarn(Integer minEarn) {
        return minEarn != null ? QPolicy.policy.minEarn.loe(minEarn) : null;
    }

    private BooleanExpression lteMaxEarn(Integer maxEarn) {
        return maxEarn != null ? QPolicy.policy.maxEarn.goe(maxEarn) : null;
    }

    private BooleanExpression inEducations(List<Education> educations) {
        return (educations != null && !educations.isEmpty()) ? QPolicy.policy.education.any().in(educations) : null;
    }

    private BooleanExpression inMajors(List<Major> majors) {
        return (majors != null && !majors.isEmpty()) ? QPolicy.policy.major.any().in(majors) : null;
    }

    private BooleanExpression inEmployments(List<Employment> employments) {
        return (employments != null && !employments.isEmpty()) ? QPolicy.policy.employment.any().in(employments) : null;
    }

    private BooleanExpression inSpecializations(List<Specialization> specializations) {
        return (specializations != null && !specializations.isEmpty()) ? QPolicy.policy.specialization.any().in(specializations) : null;
    }
}