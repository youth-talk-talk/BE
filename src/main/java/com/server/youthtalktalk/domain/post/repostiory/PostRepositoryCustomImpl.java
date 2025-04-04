package com.server.youthtalktalk.domain.post.repostiory;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.QBlock;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.QPost;
import com.server.youthtalktalk.domain.post.entity.QReview;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.report.entity.QPostReport;
import com.server.youthtalktalk.domain.scrap.entity.QScrap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QPost post = QPost.post;
    private final QReview review = QReview.review;
    private final QBlock block = QBlock.block;
    private final QScrap scrap = QScrap.scrap;
    private final QPostReport report = QPostReport.postReport;

    /** 모든 게시글 검색 */
    @Override
    public Page<Post> findAllPosts(Member member, Pageable pageable) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(postConditionsExcludeReportAndBlocked())
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(postConditionsExcludeReportAndBlocked())
                .fetchOne();

        return new PageImpl<>(posts, pageable, total == null ? 0 : total);
    }

    /** 조회수별 게시글 검색 */
    @Override
    public List<Post> findTopPostsByView(Member member, int top) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(postConditionsExcludeReportAndBlocked())
                .orderBy(post.view.desc(), post.id.desc())
                .limit(top)
                .fetch();

        return posts;
    }

    /** 모든 게시글 키워드 검색 */
    @Override
    public Page<Post> findAllPostsByKeyword(Member member, String keyword, Pageable pageable) {
        StringTemplate deleteSpaces = Expressions.stringTemplate("function('REPLACE', {0}, {1}, {2})",post.title, " ", "");
        // 키워드에서 공백을 제거한 후 LIKE 조건을 적용
        String keywordWithoutSpaces = "%" + keyword.replace(" ", "") + "%";
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(postConditionsExcludeReportAndBlocked().and(deleteSpaces.like(keywordWithoutSpaces)))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(postConditionsExcludeReportAndBlocked().and(deleteSpaces.like(keywordWithoutSpaces)))
                .fetchOne();

        return new PageImpl<>(posts, pageable, total == null ? 0 : total);
    }

    /** 나의 모든 게시글 검색*/
    @Override
    public Page<Post> findAllPostsByWriter(Pageable pageable, Member writer) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(post.writer.eq(writer))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.writer.eq(writer))
                .fetchOne();

        return new PageImpl<>(posts, pageable, total == null ? 0 : total);
    }

    /** 카테고리별 리뷰 검색 */
    @Override
    public Page<Post> findAllReviewsByCategory(Member member, List<Category> categories, Pageable pageable) {
        List<Post> reviews = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(reviewConditionsExcludeReportAndBlocked()
                        .and(JPAExpressions.treat(post, QReview.class).policy.category.in(categories)))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(reviewConditionsExcludeReportAndBlocked()
                        .and(JPAExpressions.treat(post, QReview.class).policy.category.in(categories)))
                .fetchOne();

        return new PageImpl<>(reviews, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Post> findTopReviewsByCategoryAndView(Member member, List<Category> categories, int top) {
        List<Post> reviews = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(reviewConditionsExcludeReportAndBlocked()
                        .and(JPAExpressions.treat(post, QReview.class).policy.category.in(categories)))
                .orderBy(post.view.desc(), post.id.desc())
                .limit(top)
                .fetch();

        return reviews;
    }

    /** 모든 리뷰 키워드 검색 */
    @Override
    public Page<Post> findAllReviewsByKeyword(Member member, String keyword, Pageable pageable) {
        StringTemplate deleteSpaces = Expressions.stringTemplate("function('REPLACE', {0}, {1}, {2})",post.title, " ", "");
        // 키워드에서 공백을 제거한 후 LIKE 조건을 적용
        String keywordWithoutSpaces = "%" + keyword.replace(" ", "") + "%";
        List<Post> reviews = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(reviewConditionsExcludeReportAndBlocked()
                        .and(deleteSpaces.like(keywordWithoutSpaces)))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .where(reviewConditionsExcludeReportAndBlocked()
                        .and(deleteSpaces.like(keywordWithoutSpaces)))
                .fetchOne();

        return new PageImpl<>(reviews, pageable, total == null ? 0 : total);
    }

    /** 스크랩한 게시글 검색*/
    @Override
    public Page<Post> findAllByScrap(Member member, Pageable pageable) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .join(scrap).on(post.id.eq(scrap.itemId))
                .where(report.id.isNull().and(block.id.isNull()).and(scrap.member.eq(member)))
                .orderBy(scrap.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(block).on(blockJoinWithPost(member))
                .leftJoin(report).on(reportJoinWithPost(member))
                .join(scrap).on(post.id.eq(scrap.itemId))
                .where(report.id.isNull().and(block.id.isNull()).and(scrap.member.eq(member)))
                .fetchOne();

        return new PageImpl<>(posts, pageable, total == null ? 0 : total);
    }

    BooleanExpression blockJoinWithPost(Member member){
        return post.writer.eq(block.blockedMember)
                .and(block.member.eq(member));
    }

    BooleanExpression reportJoinWithPost(Member member){
        return post.eq(report.post)
                .and(report.reporter.eq(member));
    }

    BooleanExpression postConditionsExcludeReportAndBlocked() {
        return post.instanceOf(Post.class) // Post 타입 확인
                .and(report.id.isNull()) // report.id가 null인 경우
                .and(block.id.isNull()); // block.id가 null인 경우
    }

    BooleanExpression reviewConditionsExcludeReportAndBlocked() {
        return post.instanceOf(Review.class) // Post 타입 확인
                .and(report.id.isNull()) // report.id가 null인 경우
                .and(block.id.isNull()); // block.id가 null인 경우
    }

}
