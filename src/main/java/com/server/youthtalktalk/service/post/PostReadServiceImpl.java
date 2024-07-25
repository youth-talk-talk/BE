package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.Scrap;
import com.server.youthtalktalk.domain.member.Member;
import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.domain.post.Post;
import com.server.youthtalktalk.domain.post.Review;
import com.server.youthtalktalk.dto.post.PostListRepDto;
import com.server.youthtalktalk.dto.post.PostRepDto;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.repository.PostRepository;
import com.server.youthtalktalk.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.server.youthtalktalk.dto.post.PostListRepDto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostReadServiceImpl implements PostReadService {
    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;

    /** 게시글, 리뷰 상세 조회 */
    @Override
    @Transactional
    public PostRepDto getPostById(Long postId, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        postRepository.save(post.toBuilder().view(post.getView()+1).build());
        log.info("게시글 조회 성공, postId = {}", postId);
        return post.toPostRepDto();
    }

    /** 게시글 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllPost(Pageable pageable,Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        Page<Post> postPopularPage = postRepository.findAllPostsByView(PageRequest.of(0, 5)); // 상위 5개만
        Page<Post> postPage = postRepository.findAllPosts(pageRequest);

        return toPostListRepDto(postPopularPage.getContent(),postPage.getContent(),member);
    }

    /** 리뷰 카테고리별 전체 조회 */
    @Override
    @Transactional
    public PostListRepDto getAllReviewByCategory(Pageable pageable, List<String> category,Member member) {
        List<Category> categories = new ArrayList<>();
        for(String categoryName : category){
            categories.add(Category.valueOf(categoryName));
        }
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        Page<Post> reviewPopularPage = postRepository.findAllReviewsByCategoryAndView(categories,PageRequest.of(0, 5)); // 상위 5개만
        Page<Post> reviewPage = postRepository.findAllReviewsByCategory(categories,pageRequest);

        return toPostListRepDto(reviewPopularPage.getContent(),reviewPage.getContent(),member);
    }

    /** 나의 게시글, 리뷰 전체 조회 */
    @Override
    @Transactional
    public List<PostListDto> getAllMyPost(Pageable pageable, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        List<Post> postList= postRepository.findAllPostsByWriter(pageRequest, member).getContent();

        List<PostListDto> result = new ArrayList<>();
        postList.forEach(post->result.add(toPostDto(post,member)));

        log.info("나의 게시글 조회 memberId = {}", member.getId());
        return result;
    }

    /** 게시글, 리뷰 키워드 검색 */
    @Override
    @Transactional
    public List<PostListDto> getAllPostByKeyword(Pageable pageable, String type, String keyword, Member member) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC,"id"));
        List<Post> postList=null;
        keyword = keyword.replaceAll("\\s", ""); // 키워드 공백 제거
        if(type.equals("post")){
            postList = postRepository.findAllPostsByKeyword(keyword,pageRequest).getContent();
        }
        else if(type.equals("review")){
            postList = postRepository.findAllReviewsByKeyword(keyword,pageRequest).getContent();
        }
        else throw new InvalidValueException(BaseResponseCode.INVALID_INPUT_VALUE);

        List<PostListDto> result = new ArrayList<>();
        postList.forEach(post -> result.add(toPostDto(post,member)));

        log.info("게시글 키워드 검색 성공 keyword = {} type = {}", keyword, type);
        return result;
    }

    @Override
    @Transactional
    public List<PostListDto> getScrapPostList(Pageable pageable,Member member) {
        List<Post> postList = postRepository.findAllByScrap(member, pageable).getContent();
        return postList.stream().map(post -> toPostDto(post,member)).toList();
    }

    public PostListRepDto toPostListRepDto(List<Post> topList,List<Post> postList, Member member) {
        List<PostListDto> top5_posts = new ArrayList<>();
        topList.forEach(post -> top5_posts.add(toPostDto(post, member)));
        List<PostListDto> other_posts = new ArrayList<>();
        postList.forEach(post -> other_posts.add(toPostDto(post, member)));

        return PostListRepDto.builder()
                .top5_posts(top5_posts)
                .other_posts(other_posts)
                .build();
    }

    public PostListDto toPostDto(Post post, Member member) {
        return PostListDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writerId(post.getWriter() == null ? null : post.getWriter().getId())
                .policyId(post instanceof Review ? ((Review) post).getPolicy().getPolicyId() : null)
                .policyTitle(post instanceof Review ? ((Review)post).getPolicy().getTitle() : null )
                .comments(post.getPostComments().size())
                .scrap(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),post.getId().toString(),ItemType.POST))
                .scraps(scrapRepository.findAllByItemIdAndItemType(post.getId().toString(), ItemType.POST).size())
                .build();
    }
}
