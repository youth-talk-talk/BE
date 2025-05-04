package com.server.youthtalktalk.service.policy;

import static com.server.youthtalktalk.domain.ItemType.POLICY;
import static com.server.youthtalktalk.domain.ItemType.POST;
import static com.server.youthtalktalk.domain.member.entity.Role.*;
import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.LOCAL;
import static com.server.youthtalktalk.domain.policy.entity.RepeatCode.PERIOD;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.MARRIED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.SINGLE;
import static com.server.youthtalktalk.domain.policy.entity.region.Region.SEOUL;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.PolicyListResponseDto;
import com.server.youthtalktalk.domain.policy.dto.PolicyWithReviewsDto;
import com.server.youthtalktalk.domain.policy.dto.ReviewInPolicyDto;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Department;
import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyQueryRepository;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.service.PolicyService;
import com.server.youthtalktalk.domain.policy.service.PolicyServiceImpl;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustomImpl;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {
    @Mock
    private MemberService memberService;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PostRepositoryCustomImpl postRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private final Member member = Member.builder().role(USER).build();
    private final Department dept = Department.builder().code("0000000").name("deptName").image_url("image.png").build();
    private static final long RECENT_VIEW_MAX_LEN = 10;

    @Test
    @DisplayName("인기 정책 5개와 각각의 인기 후기글 3개, 스크랩 수를 포함한 DTO를 반환한다.")
    void testGetTop5PoliciesWithReviews() {
        // given
        // 1. 조회수가 서로 다른 정책 5개 mock
        List<Policy> policies = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> Policy.builder()
                        .policyId((long) i)
                        .view(i * 10L) // 10, 20, ..., 50
                        .title("title" + i)
                        .department(dept)
                        .build())
                .sorted(Comparator.comparingLong(Policy::getView).reversed()) // 조회수 내림차순으로 정렬
                .toList();

        when(policyRepository.findTop5ByOrderByViewDescPolicyNumDesc()).thenReturn(policies);

        // 2. 각 정책에 대한 리뷰 3개씩 mock
        for (Policy policy : policies) {
            List<Review> reviews = IntStream.rangeClosed(1, 3)
                    .mapToObj(j -> {
                        Review review = mock(Review.class);
                        when(review.getId()).thenReturn(policy.getPolicyId() * 100 + (long) j);
                        when(review.getTitle()).thenReturn("Review " + j);
                        when(review.getContents()).thenReturn(createContent("후기내용".repeat(20)));
                        when(review.getPostComments()).thenReturn(List.of(new PostComment(review), new PostComment(review)));
                        when(review.getCreatedAt()).thenReturn(LocalDateTime.now());
                        return review;
                    })
                    .toList();

            when(postRepository.findTopReviewsByPolicy(member, policy, 3)).thenReturn(reviews);

            // 3. 각 리뷰에 대한 스크랩 수
            for (Post review : reviews) {
                when(scrapRepository.countByItemTypeAndItemId(POST, review.getId())).thenReturn(5L);
            }
        }

        // when
        List<PolicyWithReviewsDto> result = policyService.getTop5PoliciesWithReviews(member);

        // then
        assertThat(result).hasSize(5); // 정책 5개
        for (int i = 0; i < result.size(); i++) {
            PolicyWithReviewsDto dto = result.get(i);
            assertThat(dto.policyId()).isEqualTo(policies.get(i).getPolicyId());
            assertThat(dto.title()).isEqualTo(policies.get(i).getTitle());
            assertThat(dto.imgUrl()).isEqualTo(policies.get(i).getDepartment().getImage_url());
            assertThat(dto.reviews()).hasSize(3); // 정책별 후기 3개
            for (ReviewInPolicyDto review : dto.reviews()) {
                assertThat(review.scrapCount()).isEqualTo(5L);
                assertThat(review.commentCount()).isEqualTo(2L);
                assertThat(review.contentPreview()).endsWith("...");
            }
        }
    }

    @Test
    @DisplayName("정책 조회 시 최근 본 정책 목록 추가")
    void successAddRecentViewedPolicies(){
        // Given
        List<Long> recentViewedPolicyIds = new ArrayList<>();
        for(long i = 1; i <= RECENT_VIEW_MAX_LEN; i++){
            recentViewedPolicyIds.add(i);
        }
        Member member = Member.builder().recentViewedPolicies(recentViewedPolicyIds).role(USER).build();

        Policy viewedPolicy = Policy.builder()
                .policyId(1L)
                .view(1)
                .title("title")
                .region(SEOUL)
                .category(Category.JOB)
                .marriage(MARRIED)
                .institutionType(LOCAL)
                .department(dept)
                .build();

        // When
        when(memberService.getCurrentMember()).thenReturn(member);
        doReturn(Optional.ofNullable(viewedPolicy)).when(policyRepository).findByPolicyId(1L);

        doReturn(viewedPolicy.toBuilder().view(2).build()).when(policyRepository).save(any());
        when(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), viewedPolicy.getPolicyId(), POLICY)).thenReturn(true);

        policyService.getPolicyDetail(viewedPolicy.getPolicyId());

        // Then
        List<Long> recentViewedPolicies = member.getRecentViewedPolicies();
        int size = recentViewedPolicies.size();
        long first = recentViewedPolicies.get(0);
        long last = recentViewedPolicies.get(size - 1);
        assertThat(recentViewedPolicies).hasSize((int) RECENT_VIEW_MAX_LEN);

        assertThat(last).isEqualTo(1L); // 새로 추가된 값
        assertThat(first).isEqualTo(2L); // 최대 사이즈 유지를 위해 가장 오래된 정책은 제거됨
        // 중복 제거 테스트
        assertThat(recentViewedPolicies.stream().filter(id -> id.equals(1L)).count()).isEqualTo(1);
    }

    @Test
    @DisplayName("최근 본 정책 조회 성공")
    void successGetRecentViewedPolicies(){
        // Given
        List<Long> recentViewedPolicyIds = new ArrayList<>();
        List<Policy> recentViewedPolicies = new ArrayList<>();
        for(long i = 1; i <= RECENT_VIEW_MAX_LEN; i++){
            recentViewedPolicyIds.add(i);
            recentViewedPolicies.add(Policy.builder()
                            .policyId(i)
                            .view(1)
                            .title("title" + i)
                            .department(dept)
                            .build());
        }

        Member member = Member.builder().recentViewedPolicies(recentViewedPolicyIds).role(USER).build();

        // When
        when(memberService.getCurrentMember()).thenReturn(member);
        when(policyRepository.findAllByPolicyIdIn(member.getRecentViewedPolicies())).thenReturn(recentViewedPolicies);
        for (Policy policy : recentViewedPolicies) {
            when(scrapRepository.countByItemTypeAndItemId(ItemType.POLICY, policy.getPolicyId()))
                    .thenReturn(1L);
            when(scrapRepository.existsByMemberIdAndItemIdAndItemType(member.getId(), policy.getPolicyId(), ItemType.POLICY))
                    .thenReturn(true);
        }

        List<PolicyListResponseDto> result = policyService.getRecentViewedPolicies();
        // Then
        assertThat(result.size()).isEqualTo(RECENT_VIEW_MAX_LEN);
        for(long i = 0; i < RECENT_VIEW_MAX_LEN; i++){
            PolicyListResponseDto response = result.get((int)i);
            Long expectedId = RECENT_VIEW_MAX_LEN - i;
            assertThat(response.getPolicyId()).isEqualTo(expectedId);
            assertThat(response.getTitle()).isEqualTo("title" + expectedId);
            assertThat(response.getScrapCount()).isEqualTo(1L);
            assertThat(response.isScrap()).isTrue();
        }
    }

    private List<Content> createContent(String content){
        return new ArrayList<>(List.of(Content.builder()
                .content(content)
                .type(ContentType.TEXT)
                .build()));
    }
}
