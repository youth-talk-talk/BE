package com.server.youthtalktalk.domain.post.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.post.dto.PostListRepDto;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import com.server.youthtalktalk.domain.post.dto.ReviewListRepDto;
import com.server.youthtalktalk.domain.post.dto.ReviewListRepDto.ReviewListDto;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.post.BlockedMemberPostAccessDeniedException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.ReportedPostAccessDeniedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.server.youthtalktalk.domain.ItemType.*;
import static com.server.youthtalktalk.domain.comment.service.CommentServiceImpl.TIME_FORMAT;
import static com.server.youthtalktalk.domain.post.dto.PostListRepDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostReadServiceImpl implements PostReadService {

    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;
    private final ReportRepository reportRepository;
    private final BlockRepository blockRepository;
    private final PostRepositoryCustom postRepositoryCustom;

    private static final int TOP = 5;
    private static final int CONTENT_PREVIEW_MAX_LEN = 50;

    /** 게시글, 리뷰 상세 조회 */
    @Override
    @Transactional
    public PostRepDto getPostById(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(reportRepository.existsByPostAndReporter(post,member)){
            throw new ReportedPostAccessDeniedException();
        }

        if(blockRepository.existsByMemberAndBlockedMember(member, post.getWriter())){
            throw new BlockedMemberPostAccessDeniedException();
        }

        postRepository.save(post.toBuilder().view(post.getView()+1).build());
        log.info("게시글 조회 성공, postId = {}", postId);
        return post.toPostRepDto(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),post.getId(), POST));
    }

    /** 게시글 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllPost(Pageable pageable, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Post> popularPostList = postRepositoryCustom.findTopPostsByView(member, TOP); // 상위 5개만
        Page<Post> postPage = postRepositoryCustom.findAllPosts(member, pageRequest);
        return toPostListRepDto(popularPostList, postPage.getContent(), member);
    }

    /** 리뷰 카테고리별 전체 조회 */
    @Override
    @Transactional
    public ReviewListRepDto getAllReviewByCategory(Pageable pageable, List<Category> categories, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        List<Post> popularReviewList = postRepositoryCustom.findTopReviewsByView(member, TOP); // 상위 5개만

        Page<Post> reviewPage;
        if (categories == null || categories.isEmpty()) { // 카테고리 전체를 선택한 경우
            reviewPage = postRepositoryCustom.findAllReviews(member, pageRequest);
        } else { // 특정 카테고리만 선택한 경우
            reviewPage = postRepositoryCustom.findAllReviewsByCategory(member, categories, pageRequest);
        }

        return toReviewListRepDto(popularReviewList, reviewPage.getContent(), member);
    }

    /** 나의 게시글, 리뷰 전체 조회 */
    @Override
    @Transactional
    public PostListResponse getAllMyPost(Pageable pageable, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Post> postList = postRepositoryCustom.findAllPostsByWriter(pageRequest, member);
        List<PostListDto> result = postList.stream().map(post -> toPostDto(post, member)).toList();

        log.info("나의 게시글 조회 memberId = {}", member.getId());
        return PostListResponse.builder()
                .posts(result)
                .page(pageRequest.getPageNumber())
                .total(postList.getTotalElements())
                .build();
    }

    /** 게시글, 리뷰 키워드 검색 */
    @Override
    @Transactional
    public PostListResponse getAllPostByKeyword(Pageable pageable, String type, String keyword, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Post> postList = switch (type) {
            case "post" -> postRepositoryCustom.findAllPostsByKeyword(member, keyword, pageRequest);
            case "review" -> postRepositoryCustom.findAllReviewsByKeyword(member, keyword, pageRequest);
            default -> throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);
        };
        List<PostListDto> result = postList.getContent().stream().map(post -> toPostDto(post, member)).toList();

        log.info("게시글 키워드 검색 성공 keyword = {} type = {}", keyword, type);
        return PostListResponse.builder()
                .posts(result)
                .page(postList.getNumber())
                .total(postList.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public PostListResponse getScrapPostList(Pageable pageable, Member member) {
        Page<Post> postList = postRepositoryCustom.findAllByScrap(member, pageable);
        List<PostListDto> result = postList.stream().map(post -> toPostDto(post, member)).toList();
        log.info("스크랩한 게시글 전체 조회 성공");
        return PostListResponse.builder()
                .posts(result)
                .page(postList.getNumber())
                .total(postList.getTotalElements())
                .build();
    }

    public PostListRepDto toPostListRepDto(List<Post> topList,List<Post> postList, Member member) {
        List<PostListDto> top5_posts = new ArrayList<>();
        topList.forEach(post -> top5_posts.add(toPostDto(post, member)));
        List<PostListDto> other_posts = new ArrayList<>();
        postList.forEach(post -> other_posts.add(toPostDto(post, member)));

        return builder()
                .top5Posts(top5_posts)
                .allPosts(other_posts)
                .build();
    }

    public ReviewListRepDto toReviewListRepDto(List<Post> topList, List<Post> postList, Member member) {
        List<ReviewListDto> top5Posts = new ArrayList<>();
        topList.forEach(post -> top5Posts.add(toReviewDto(post, member)));
        List<ReviewListDto> otherPosts = new ArrayList<>();
        postList.forEach(post -> otherPosts.add(toReviewDto(post, member)));

        return ReviewListRepDto.builder().top5Posts(top5Posts).allPosts(otherPosts).build();
    }

    public PostListDto toPostDto(Post post, Member member) {
        return PostListDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .writerId(post.getWriter() == null ? null : post.getWriter().getId())
                .policyId(post instanceof Review ? ((Review) post).getPolicy().getPolicyId() : null)
                .policyTitle(post instanceof Review ? ((Review)post).getPolicy().getTitle() : null )
                .comments(post.getPostComments().size())
                .contentPreview(createContentSnippet(post.getContents().get(0).getContent()))
                .scrapCount(scrapRepository.findAllByItemIdAndItemType(post.getId(), POST).size())
                .scrap(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),post.getId(), POST))
                .createdAt(post.getCreatedAt().format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
                .build();
    }

    // 리뷰 전용 DTO - TODO 게시글 dto 코드 리팩토링
    public ReviewListDto toReviewDto(Post post, Member member) {
        Review review = (Review) post;
        Long reviewId = review.getId();
        return ReviewListDto.builder()
                .postId(reviewId)
                .writerId(review.getWriter() == null ? -1L : review.getWriter().getId())
                .title(review.getTitle())
                .contentPreview(createContentSnippet(review.getContents().get(0).getContent()))
                .policyId(review.getPolicy().getPolicyId())
                .policyTitle(review.getPolicy().getTitle())
                .comments(review.getPostComments().size())
                .scrapCount(scrapRepository.findAllByItemIdAndItemType(review.getId(), POST).size())
                .scrap(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), reviewId, POST))
                .category(review.getPolicy().getCategory())
                .createdAt(review.getCreatedAt().format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
                .build();
    }

    private String createContentSnippet(String content) {
        return content.length() > CONTENT_PREVIEW_MAX_LEN
                ? content.substring(0, CONTENT_PREVIEW_MAX_LEN) + "..."
                : content;
    }

    /**
     * 조회수 top4 게시글 조회
     */
    @Override
    public List<PostListDto> getTopPostsByView(Member member) {
        List<Post> postList = postRepositoryCustom.findTopReviewsAndPostsByView(member, 4);
        List<PostListDto> result = new ArrayList<>();
        postList.forEach(post -> result.add(toPostDto(post, member)));
        return result;
    }
}
