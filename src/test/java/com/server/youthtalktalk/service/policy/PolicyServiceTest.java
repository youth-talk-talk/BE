package com.server.youthtalktalk.service.policy;

import static com.server.youthtalktalk.domain.ItemType.POLICY;
import static com.server.youthtalktalk.domain.ItemType.POST;
import static com.server.youthtalktalk.domain.member.entity.Role.*;
import static com.server.youthtalktalk.domain.policy.entity.Category.DWELLING;
import static com.server.youthtalktalk.domain.policy.entity.Category.EDUCATION;
import static com.server.youthtalktalk.domain.policy.entity.Category.JOB;
import static com.server.youthtalktalk.domain.policy.entity.Category.LIFE;
import static com.server.youthtalktalk.domain.policy.entity.Category.PARTICIPATION;
import static com.server.youthtalktalk.domain.policy.entity.InstitutionType.LOCAL;
import static com.server.youthtalktalk.domain.policy.entity.RepeatCode.PERIOD;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.MARRIED;
import static com.server.youthtalktalk.domain.policy.entity.condition.Marriage.SINGLE;
import static com.server.youthtalktalk.domain.policy.entity.region.Region.SEOUL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.PolicyListResponseDto;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.Department;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.repository.PolicyRepository;
import com.server.youthtalktalk.domain.policy.service.PolicyServiceImpl;
import com.server.youthtalktalk.domain.post.entity.Content;
import com.server.youthtalktalk.domain.post.entity.ContentType;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private final Department dept = Department.builder().code("0000000").name("deptName").image_url("image.png").build();
    private static final long RECENT_VIEW_MAX_LEN = 10;

    @Test
    @DisplayName("관심지역이 전국일 때 우리지역 인기 정책을 성공적으로 반환한다.")
    void testGetPopularPoliciesInArea_shouldUseFindAllWhenRegionIsNationwide() {
        // given
        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.NATIONWIDE);
        when(member.getId()).thenReturn(1L);

        List<Policy> mockPolicies = createPolicy(2);
        Page<Policy> mockPage = new PageImpl<>(mockPolicies);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        when(policyRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        // when
        List<PolicyListResponseDto> result = policyService.getPopularPoliciesInArea(member);

        // then
        // 관심지역이 전국일 때 findAll 호출 여부 검증
        verify(policyRepository).findAll(pageRequestCaptor.capture());

        // 조회한 페이지 정보 검증 (페이지 사이즈, 정렬)
        PageRequest captured = pageRequestCaptor.getValue();
        List<Sort.Order> orders = new ArrayList<>();
        captured.getSort().forEach(orders::add);

        assertEquals(20, captured.getPageSize());
        assertEquals(2, orders.size());
        assertEquals("view", orders.get(0).getProperty()); // 1순위 조회수 순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());
        assertEquals("policyNum", orders.get(1).getProperty()); // 2순위 최신순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(1).getDirection());
        assertNotNull(result); // DTO 필드 검증은 여기서 하지 않음
    }

    @Test
    @DisplayName("관심지역이 특정 지역일 때 우리지역 인기 정책을 성공적으로 반환한다.")
    void testGetPopularPoliciesInArea_shouldUseFindByRegionWhenRegionIsSpecific() {
        // given
        Member member = mock(Member.class);
        when(member.getRegion()).thenReturn(Region.SEOUL);
        when(member.getId()).thenReturn(1L);

        List<Policy> mockPolicies = createPolicy(2);
        Page<Policy> mockPage = new PageImpl<>(mockPolicies);
        ArgumentCaptor<PageRequest> pageRequestCaptor = ArgumentCaptor.forClass(PageRequest.class);
        when(policyRepository.findTop20ByRegion(eq(Region.SEOUL), any(PageRequest.class))).thenReturn(mockPage);

        // when
        List<PolicyListResponseDto> result = policyService.getPopularPoliciesInArea(member);

        // then
        // 관심지역이 특정 지역일 때 findTop20ByRegion 호출 여부 검증
        verify(policyRepository).findTop20ByRegion(eq(Region.SEOUL), pageRequestCaptor.capture());

        // 조회한 페이지 정보 검증 (페이지 사이즈, 정렬)
        PageRequest captured = pageRequestCaptor.getValue();
        List<Sort.Order> orders = new ArrayList<>();
        captured.getSort().forEach(orders::add);

        assertEquals(20, captured.getPageSize());
        assertEquals(2, orders.size());
        assertEquals("view", orders.get(0).getProperty()); // 1순위 조회수 순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());
        assertEquals("policyNum", orders.get(1).getProperty()); // 2순위 최신순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(1).getDirection());
        assertNotNull(result); // DTO 필드 검증은 여기서 하지 않음
    }

    @Test
    @DisplayName("인기순 정렬일 때, 카테고리별 새로운 정책을 성공적으로 반환한다.")
    void testGetNewPoliciesByCategory_withPopularSort() {
        // given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        LocalDateTime from = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime to = LocalDate.now().plusDays(1).atStartOfDay();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        when(policyRepository.findByCreatedAtBetween(eq(from), eq(to), any(Sort.class))).thenReturn(Collections.emptyList());
        when(policyRepository.findByCreatedAtBetweenAndCategory(eq(from), eq(to), any(Category.class), any(Sort.class))).thenReturn(Collections.emptyList());

        // when
        policyService.getNewPoliciesByCategory(member, "POPULAR");

        // then
        verify(policyRepository).findByCreatedAtBetween(eq(from), eq(to), sortCaptor.capture());
        Sort sort = sortCaptor.getValue();
        List<Sort.Order> orders = new ArrayList<>();
        sort.forEach(orders::add);

        // 정렬 기준 검증
        assertEquals(2, orders.size());
        assertEquals("view", orders.get(0).getProperty()); // 1순위 조회수 순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());
        assertEquals("policyNum", orders.get(1).getProperty()); // 2순위 최신순 정렬
        assertEquals(Sort.Direction.DESC, orders.get(1).getDirection());

        // 카테고리별 조회가 모두 호출되었는지 검증
        verify(policyRepository).findByCreatedAtBetween(eq(from), eq(to), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(JOB), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(DWELLING), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(EDUCATION), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(LIFE), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(PARTICIPATION), any(Sort.class));
    }

    @Test
    @DisplayName("최신순 정렬일 때, 카테고리별 새로운 정책을 성공적으로 반환한다.")
    void testGetNewPoliciesByCategory_withLatestSort() {
        // given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);

        LocalDateTime from = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime to = LocalDate.now().plusDays(1).atStartOfDay();

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        // 모든 policyRepository 메서드는 빈 리스트 반환
        when(policyRepository.findByCreatedAtBetween(eq(from), eq(to), any(Sort.class))).thenReturn(Collections.emptyList());
        when(policyRepository.findByCreatedAtBetweenAndCategory(eq(from), eq(to), any(Category.class), any(Sort.class))).thenReturn(Collections.emptyList());

        // when
        policyService.getNewPoliciesByCategory(member, "RECENT");

        // then
        // 정렬 기준 검증
        verify(policyRepository).findByCreatedAtBetween(eq(from), eq(to), sortCaptor.capture());
        Sort sort = sortCaptor.getValue();
        List<Sort.Order> orders = new ArrayList<>();
        sort.forEach(orders::add);

        assertEquals(1, orders.size());
        assertEquals("policyNum", orders.get(0).getProperty());
        assertEquals(Sort.Direction.DESC, orders.get(0).getDirection());

        // 카테고리별 조회가 모두 호출되었는지 검증
        verify(policyRepository).findByCreatedAtBetween(eq(from), eq(to), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(JOB), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(DWELLING), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(EDUCATION), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(LIFE), any(Sort.class));
        verify(policyRepository).findByCreatedAtBetweenAndCategory(eq(from), eq(to), eq(PARTICIPATION), any(Sort.class));
    }

    private List<Content> createContent(String content){
        return new ArrayList<>(List.of(Content.builder()
                .content(content)
                .type(ContentType.TEXT)
                .build()));
    }

    private List<Policy> createPolicy(int count){
        List<Policy> policyList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Policy policy = Policy.builder()
                    .policyId(1L)
                    .policyNum("policy" + i)
                    .title("title" + i)
                    .department(dept)
                    .region(SEOUL)
                    .category(JOB)
                    .institutionType(LOCAL)
                    .repeatCode(PERIOD)
                    .marriage(SINGLE)
                    .build();

            policyList.add(policy);
        }
        return policyList;
    }
}
