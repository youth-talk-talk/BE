package com.server.youthtalktalk.service.policy;

import static com.server.youthtalktalk.domain.ItemType.POST;
import static com.server.youthtalktalk.domain.member.entity.Role.*;
import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.LOCAL;
import static com.server.youthtalktalk.domain.policy.entity.RepeatCode.PERIOD;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.SINGLE;
import static com.server.youthtalktalk.domain.policy.entity.region.Region.SEOUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.server.youthtalktalk.domain.ItemType;
import com.server.youthtalktalk.domain.comment.entity.PostComment;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.policy.dto.PolicyWithReviewsDto;
import com.server.youthtalktalk.domain.policy.dto.ReviewInPolicyDto;
import com.server.youthtalktalk.domain.policy.entity.Department;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.repository.PolicyQueryRepository;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.service.PolicyService;
import com.server.youthtalktalk.domain.policy.service.PolicyServiceImpl;
import com.server.youthtalktalk.domain.post.entity.Post;
import com.server.youthtalktalk.domain.post.entity.Review;
import com.server.youthtalktalk.domain.post.repostiory.PostRepository;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustom;
import com.server.youthtalktalk.domain.post.repostiory.PostRepositoryCustomImpl;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
    private PolicyRepository policyRepository;

    @Mock
    private PostRepositoryCustomImpl postRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private final Member member = Member.builder().role(USER).build();

    @Test
    @DisplayName("인기 정책 5개와 각각의 인기 후기글 3개, 스크랩 수를 포함한 DTO를 반환한다.")
    void testGetTop5PoliciesWithReviews() {
        // given
        // 1. 조회수가 서로 다른 정책 5개 mock
        Department dept = Department.builder().code("0000000").name("deptName").image_url("image.png").build();

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
                        when(review.getContent()).thenReturn("후기내용".repeat(20));
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
}
