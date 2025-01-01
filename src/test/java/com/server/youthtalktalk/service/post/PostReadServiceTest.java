package com.server.youthtalktalk.service.post;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.member.repository.BlockRepository;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.post.dto.PostListRepDto;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.post.service.PostReadServiceImpl;
import com.server.youthtalktalk.domain.report.repository.ReportRepository;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.entity.Role;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.dto.PostRepDto;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.scrap.entity.Scrap;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.global.response.exception.InvalidValueException;
import com.server.youthtalktalk.global.response.exception.post.BlockedMemberPostAccessDeniedException;
import com.server.youthtalktalk.global.response.exception.post.PostNotFoundException;
import com.server.youthtalktalk.global.response.exception.post.ReportedPostAccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.server.youthtalktalk.domain.post.dto.PostListRepDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PostReadServiceTest {
    @InjectMocks
    private PostReadServiceImpl postReadService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private BlockRepository blockRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private PostRepositoryCustom postRepositoryCustom;

    private static final int LEN = 5;
    private static final int TOP = 5;

    @Test
    @DisplayName("게시글 상세 조회 성공")
    void successGetPostById(){
        Member member1 = createMember("member1",1L);
        Member member2 = createMember("member2",2L);
        // Given
        Post post = Post.builder()
                .title("post")
                .id(1L)
                .contents(new ArrayList<>(List.of(createContent())))
                .view(1L)
                .writer(member2)
                .build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reportRepository.existsByPostAndReporter(post,member1)).thenReturn(false);
        when(blockRepository.existsByMemberAndBlockedMember(member1, post.getWriter())).thenReturn(false);
        when(scrapRepository.existsByMemberIdAndItemIdAndItemType(member1.getId(),post.getId().toString(), ItemType.POST))
                .thenReturn(true);
        // When
        PostRepDto postRepDto = postReadService.getPostById(post.getId(),member1);
        // Then
        assertThat(postRepDto.getPostId()).isEqualTo(post.getId());
        assertThat(postRepDto.getTitle()).isEqualTo(post.getTitle());
        assertThat(postRepDto.getView()).isEqualTo(1L);
        assertThat(postRepDto.getWriterId()).isEqualTo(member2.getId());
        assertThat(postRepDto.isScrap()).isTrue();
        assertThat(postRepDto.getContentList().size()).isEqualTo(1);

        verify(postRepository).findById(post.getId());
        verify(reportRepository).existsByPostAndReporter(post,member1);
        verify(blockRepository).existsByMemberAndBlockedMember(member1,post.getWriter());
        verify(scrapRepository).existsByMemberIdAndItemIdAndItemType(member1.getId(),post.getId().toString(), ItemType.POST);
    }

    @Test
    @DisplayName("존재하지 않는 게시물 조회 실패")
    void failGetPostByIdIfNotExist(){
        Member member1 = createMember("member1",1L);
        assertThatThrownBy(() -> postReadService.getPostById(Long.MAX_VALUE,member1))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("신고한 게시글 조회 실패")
    void failGetPostByIdIfReportedPost(){
        // Given
        Post post = Post.builder()
                        .id(1L)
                        .build();
        Member member1 = Member.builder().build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reportRepository.existsByPostAndReporter(post, member1)).thenReturn(true);

        // When, Then
        assertThatThrownBy(() -> postReadService.getPostById(post.getId(),member1))
                .isInstanceOf(ReportedPostAccessDeniedException.class);
    }

    @Test
    @DisplayName("차단한 유저의 게시글 조회 실패")
    void failGetPostByIdIfBlockedWriter(){
        // Given
        Post post = Post.builder()
                .id(1L)
                .build();
        Member member1 = Member.builder().build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(reportRepository.existsByPostAndReporter(post, member1)).thenReturn(false);
        when(blockRepository.existsByMemberAndBlockedMember(member1, post.getWriter())).thenReturn(true);
        // When, Then
        assertThatThrownBy(() -> postReadService.getPostById(post.getId(),member1))
                .isInstanceOf(BlockedMemberPostAccessDeniedException.class);
    }

    @Test
    @DisplayName("자유 게시글 PostListDto 변환 성공")
    void successFreePostToPostDto(){
        // Given
        Member member = createMember("member",1L);
        Post post = createPost("post",1L, member, 10L);
        when(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),post.getId().toString(),ItemType.POST))
                .thenReturn(true);
        when(scrapRepository.findAllByItemIdAndItemType(post.getId().toString(), ItemType.POST))
                .thenReturn(new ArrayList<>());
        // When
        PostListDto postListDto = postReadService.toPostDto(post, member);
        // Then
        assertThat(postListDto.getPostId()).isEqualTo(1L);
        assertThat(postListDto.getTitle()).isEqualTo("post");
        assertThat(postListDto.getWriterId()).isEqualTo(1L);
        assertThat(postListDto.isScrap()).isTrue();
        assertThat(postListDto.getPolicyId()).isNull();
        assertThat(postListDto.getPolicyTitle()).isNull();
        assertThat(postListDto.getComments()).isEqualTo(0);

        verify(scrapRepository).existsByMemberIdAndItemIdAndItemType(member.getId(),post.getId().toString(),ItemType.POST);
        verify(scrapRepository).findAllByItemIdAndItemType(post.getId().toString(), ItemType.POST);
    }

    @Test
    @DisplayName("리뷰 PostListDto 변환 성공")
    void successReviewToPostDto(){
        // Given
        Member member = createMember("member",1L);
        Policy policy = Policy.builder()
                .policyId("policyId")
                .title("policy")
                .build();
        Review review = Review.builder()
                .id(1L)
                .postComments(new ArrayList<>())
                .policy(policy)
                .title("review")
                .writer(member)
                .build();
        when(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(),review.getId().toString(),ItemType.POST))
                .thenReturn(true);
        when(scrapRepository.findAllByItemIdAndItemType(review.getId().toString(), ItemType.POST))
                .thenReturn(new ArrayList<>());
        // When
        PostListDto postListDto = postReadService.toPostDto(review, member);
        // Then
        assertThat(postListDto.getPostId()).isEqualTo(1L);
        assertThat(postListDto.getTitle()).isEqualTo("review");
        assertThat(postListDto.getWriterId()).isEqualTo(member.getId());
        assertThat(postListDto.isScrap()).isTrue();
        assertThat(postListDto.getPolicyId()).isEqualTo("policyId");
        assertThat(postListDto.getPolicyTitle()).isEqualTo("policy");
        assertThat(postListDto.getComments()).isEqualTo(0);

        verify(scrapRepository).existsByMemberIdAndItemIdAndItemType(member.getId(),review.getId().toString(),ItemType.POST);
        verify(scrapRepository).findAllByItemIdAndItemType(review.getId().toString(), ItemType.POST);
    }

    @Test
    @DisplayName("게시글 전체 조회 성공")
    void successGetAllPost(){
        // Given
        Member member = createMember("member",1L);
        List<Post> posts = new ArrayList<>();
        for(long i = 0; i < LEN; i++){
            posts.add(createPost("post"+i, i, member, LEN - i));
        }
        int pageSize = 3;
        Pageable pageable1 = PageRequest.of(0, pageSize);
        Pageable pageable2 = PageRequest.of(1, pageSize);

        when(postRepositoryCustom.findTopPostsByView(member, TOP)) // 인기순(조회수)
                .thenReturn(posts);
        when(postRepositoryCustom.findAllPosts(member, pageable1))
                .thenReturn(convertListToPage(posts, pageable1));
        when(postRepositoryCustom.findAllPosts(member, pageable2))
                .thenReturn(convertListToPage(posts, pageable2));
        // When
        PostListRepDto postListRepDto1 = postReadService.getAllPost(pageable1, member);
        PostListRepDto postListRepDto2 = postReadService.getAllPost(pageable2, member);
        // Then
        assertThat(postListRepDto1.getTop5_posts().get(0).getTitle()).isEqualTo("post0"); // 인기순 (조회수)
        assertThat(postListRepDto1.getTop5_posts().get(1).getTitle()).isEqualTo("post1");
        assertThat(postListRepDto1.getTop5_posts()).hasSize(LEN);

        for(int i = 0; i < pageSize; i++){
            assertThat(postListRepDto1.getOther_posts().get(i).getTitle()).isEqualTo("post"+i);
        }
        assertThat(postListRepDto1.getOther_posts()).hasSize(pageSize);
        assertThat(postListRepDto2.getOther_posts()).hasSize(LEN - pageSize);

        verify(postRepositoryCustom, times(2)).findTopPostsByView(member, TOP);
        verify(postRepositoryCustom).findAllPosts(member, pageable1);
        verify(postRepositoryCustom).findAllPosts(member, pageable2);
    }

    @Test
    @DisplayName("카테고리별 리뷰 전체 조회")
    void successGetAllReviewByCategory(){
        // Given
        Member member = createMember("member",1L);
        Policy policy = Policy.builder()
                .policyId("policyId")
                .title("policy")
                .category(Category.JOB)
                .build();
        List<Post> posts = new ArrayList<>();
        for(long i = 0; i < LEN; i++){
            Review review = Review.builder()
                    .id(1 + i)
                    .postComments(new ArrayList<>())
                    .policy(policy)
                    .view(LEN - i)
                    .title("review" + i + 1)
                    .writer(member)
                    .build();
            posts.add(review);
        }

        List<Category> categories = new ArrayList<>(List.of(Category.JOB));
        Pageable pageable = PageRequest.of(0, LEN);

        when(postRepositoryCustom.findTopReviewsByCategoryAndView(member, categories, TOP))
                .thenReturn(posts);
        when(postRepositoryCustom.findAllReviewsByCategory(member, categories, pageable))
                .thenReturn(convertListToPage(posts, pageable));
        // When
        PostListRepDto postListRepDto = postReadService.getAllReviewByCategory(pageable, categories, member);
        // Then
        assertThat(postListRepDto.getOther_posts()).hasSize(LEN);
        assertThat(postListRepDto.getOther_posts().get(0).getPolicyTitle()).isEqualTo(policy.getTitle());
        assertThat(postListRepDto.getOther_posts().get(0).getPolicyId()).isEqualTo(policy.getPolicyId());

        verify(postRepositoryCustom).findTopReviewsByCategoryAndView(member, categories, TOP);
        verify(postRepositoryCustom).findAllReviewsByCategory(member, categories, pageable);
    }

    @Test
    @DisplayName("내가 작성한 게시글 조회 성공")
    void successGetAllMyPostTest(){
        // Given
        Member member = createMember("member",1L);
        List<Post> posts = new ArrayList<>();
        for(long i = 0; i < LEN; i++){
            posts.add(createPost("post"+i, i, member, LEN - i));
        }
        Pageable pageable = PageRequest.of(0, LEN);
        when(postRepositoryCustom.findAllPostsByWriter(pageable, member)).thenReturn(convertListToPage(posts, pageable));
        // When
        List<PostListDto> myPosts = postReadService.getAllMyPost(pageable, member);
        // Then
        assertThat(myPosts).hasSize(LEN);
        assertThat(myPosts.get(0).getWriterId()).isEqualTo(member.getId());

        verify(postRepositoryCustom).findAllPostsByWriter(pageable, member);
    }

    @Test
    @DisplayName("키워드별 전체 게시글, 리뷰 조회 성공")
    void successGetAllPostByKeyword(){
        // Given
        Member member = createMember("member",1L);
        Policy policy = Policy.builder()
                .policyId("policyId")
                .title("policy")
                .category(Category.JOB)
                .build();
        List<Post> posts = new ArrayList<>(List.of(createPost("testPost",1L, member, 1L)));
        List<Post> reviews = new ArrayList<>(List.of(Review.builder()
                .id(2L)
                .title("testReview")
                .policy(policy)
                .build()));

        Pageable pageable = PageRequest.of(0, 1);
        String keyword = "test";
        when(postRepositoryCustom.findAllPostsByKeyword(member, keyword, pageable))
                .thenReturn(convertListToPage(posts, pageable));
        when(postRepositoryCustom.findAllReviewsByKeyword(member, keyword, pageable))
                .thenReturn(convertListToPage(reviews, pageable));
        // When
        PostListResponse postsByKeyword = postReadService.getAllPostByKeyword(pageable, "post", keyword, member);
        PostListResponse reviewsByKeyword = postReadService.getAllPostByKeyword(pageable, "review", keyword, member);
        // Then
        assertThat(postsByKeyword.getPage()).isEqualTo(pageable.getPageNumber());
        assertThat(postsByKeyword.getPosts()).hasSize(1);
        assertThat(postsByKeyword.getTotal()).isEqualTo(1);
        assertThat(postsByKeyword.getPosts().get(0).getTitle()).contains(keyword);

        assertThat(reviewsByKeyword.getPage()).isEqualTo(pageable.getPageNumber());
        assertThat(reviewsByKeyword.getPosts()).hasSize(1);
        assertThat(reviewsByKeyword.getTotal()).isEqualTo(1);
        assertThat(reviewsByKeyword.getPosts().get(0).getPolicyId()).isEqualTo(policy.getPolicyId());
        assertThat(reviewsByKeyword.getPosts().get(0).getTitle()).contains(keyword);

        verify(postRepositoryCustom).findAllPostsByKeyword(member, keyword, pageable);
        verify(postRepositoryCustom).findAllReviewsByKeyword(member, keyword, pageable);
    }

    @Test
    @DisplayName("유효하지 않은 타입 파라미터로 인한 키워드별 전체 게시글, 리뷰 조회 실패")
    void failGetAllPostByKeywordIfInvalidType() {
        // Given
        Member member = createMember("member",1L);
        String type = "invalidType";
        Pageable pageable = PageRequest.of(0, 1);
        // When, Then
        assertThatThrownBy(() -> postReadService.getAllPostByKeyword(pageable, type, "test", member))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(BaseResponseCode.INVALID_INPUT_VALUE.getMessage());
    }

    @Test
    @DisplayName("내가 스크랩한 게시글 조회")
    void successGetScrapPostList(){
        // Given
        Member member = createMember("member",1L);
        List<Post> posts = new ArrayList<>();
        for(long i = 0; i < LEN; i++){
            posts.add(createPost("post"+i, i, member, LEN - i));
        }

        Pageable pageable = PageRequest.of(0, LEN);
        when(postRepositoryCustom.findAllByScrap(member, pageable)).thenReturn(convertListToPage(posts, pageable));
        for(Post post : posts){
            Scrap scrap = Scrap.builder()
                    .member(member)
                    .itemType(ItemType.POST)
                    .itemId(post.getId().toString())
                    .build();
            when(scrapRepository.findByMemberAndItemIdAndItemType(member,post.getId().toString(),ItemType.POST))
                    .thenReturn(Optional.of(scrap));
            when(scrapRepository.findAllByItemIdAndItemType(post.getId().toString(), ItemType.POST))
                    .thenReturn(new ArrayList<>(List.of(scrap)));
        }

        // When
        List<ScrapPostListDto> scrapPostList = postReadService.getScrapPostList(pageable, member);
        // Then
        ScrapPostListDto first = scrapPostList.get(0);
        assertThat(scrapPostList).hasSize(LEN);
        assertThat(first.getPostId()).isEqualTo(posts.get(0).getId());
        assertThat(first.getTitle()).isEqualTo(posts.get(0).getTitle());
        assertThat(first.getScraps()).isEqualTo(1);
        assertThat(first.getWriterId()).isEqualTo(member.getId());
        assertThat(first.getPolicyTitle()).isNull();
        assertThat(first.getPolicyId()).isNull();

        verify(postRepositoryCustom).findAllByScrap(member, pageable);
    }

    @Test
    @DisplayName("내가 스크랩한 후기 조회")
    void successGetScrapReviewList(){
        // Given
        Member member = createMember("member",1L);
        Policy policy = Policy.builder()
                .policyId("policyId")
                .title("policy")
                .category(Category.JOB)
                .build();
        List<Post> reviews = new ArrayList<>(List.of(Review.builder()
                .id(2L)
                .title("testReview")
                .policy(policy)
                .build()));


        Pageable pageable = PageRequest.of(0, 1);
        when(postRepositoryCustom.findAllByScrap(member, pageable)).thenReturn(convertListToPage(reviews, pageable));
        for(Post post : reviews){
            Scrap scrap = Scrap.builder()
                    .member(member)
                    .itemType(ItemType.POST)
                    .itemId(post.getId().toString())
                    .build();
            when(scrapRepository.findByMemberAndItemIdAndItemType(member,post.getId().toString(),ItemType.POST))
                    .thenReturn(Optional.of(scrap));
            when(scrapRepository.findAllByItemIdAndItemType(post.getId().toString(), ItemType.POST))
                    .thenReturn(new ArrayList<>(List.of(scrap)));
        }

        // When
        List<ScrapPostListDto> scrapPostList = postReadService.getScrapPostList(pageable, member);
        // Then
        ScrapPostListDto first = scrapPostList.get(0);
        assertThat(scrapPostList).hasSize(1);
        assertThat(first.getScraps()).isEqualTo(1);
        assertThat(first.getPolicyTitle()).isEqualTo(policy.getTitle());
        assertThat(first.getPolicyId()).isEqualTo(policy.getPolicyId());

        verify(postRepositoryCustom).findAllByScrap(member, pageable);
    }

    private Post createPost(String title, Long id, Member writer, Long view){
        return Post.builder()
                .id(id)
                .contents(new ArrayList<>(List.of(createContent())))
                .writer(writer)
                .view(view)
                .title(title)
                .postComments(new ArrayList<>())
                .build();
    }

    private Member createMember(String name, Long id){
        return Member.builder()
                .role(Role.USER)
                .username(name)
                .id(id)
                .build();
    }

    private Content createContent(){
        return Content.builder()
                .content("content")
                .type(ContentType.TEXT)
                .build();
    }

    private Page<Post> convertListToPage(List<Post> posts, Pageable pageable) {
        int start = (int) pageable.getOffset();
        System.out.println(start);
        int end = Math.min((start + pageable.getPageSize()), posts.size());

        List<Post> subList = posts.subList(start, end);
        return new PageImpl<>(subList, pageable, posts.size());
    }
}
