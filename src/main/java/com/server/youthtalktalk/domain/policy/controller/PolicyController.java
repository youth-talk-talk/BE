package com.server.youthtalktalk.domain.policy.controller;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.*;
import com.server.youthtalktalk.domain.policy.entity.SortOption;
import com.server.youthtalktalk.domain.policy.service.PolicyService;
import com.server.youthtalktalk.domain.post.dto.PostListRepDto.PostListDto;
import com.server.youthtalktalk.domain.post.service.PostReadService;
import com.server.youthtalktalk.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final PostReadService postService;
    private final MemberService memberService;

    /**
     * 홈 전체 조회
     * 홈 화면의 컨텐츠를 일괄 조회합니다. (우리 지역 인기정책 + 따끈따끈 새로운 정책 + 지금뜨는 정책톡톡 + 청년톡톡 BEST)
     */
    @GetMapping("/home")
    public BaseResponse<HomeResponseDto> home(@RequestParam(defaultValue = "RECENT") String sort){
        Member member = memberService.getCurrentMember();

        // 우리 지역 인기정책 데이터
        List<PolicyListResponseDto> popularPoliciesInArea = policyService.getPopularPoliciesInArea(member);

        // 따끈따끈 새로운 정책 데이터
        NewPoliciesResponseDto newPoliciesByCategory = policyService.getNewPoliciesByCategory(member, sort);

        // 지금뜨는 정책톡톡 데이터
        List<PolicyWithReviewsDto> top5PoliciesWithReviews = policyService.getTop5PoliciesWithReviews(member);

        // 청년톡톡 BEST 데이터
        List<PostListDto> bestPosts = postService.getTopPostsByView(member);

        HomeResponseDto homeResponseDto = new HomeResponseDto(popularPoliciesInArea, newPoliciesByCategory, top5PoliciesWithReviews, bestPosts);
        return new BaseResponse<>(homeResponseDto, SUCCESS);
    }

    /**
     * 조건 정책 조회
     */
    @PostMapping("/policies/search")
    public BaseResponse<PolicyPageResponseDto> getPoliciesByCondition(
            @RequestBody(required = false) SearchConditionRequestDto searchCondition,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "RECENT") SortOption sort) {
        Pageable pageable = PageRequest.of(page, size);
        PolicyPageResponseDto pageResponseDto;

        if (searchCondition == null) { // 조건이 없으면 전체 정책 조회
            pageResponseDto = policyService.getAllPolicies(pageable, sort);
        } else { // 조건이 있으면 조건 적용 조회
            pageResponseDto = policyService.getPoliciesByCondition(searchCondition, pageable, sort);
        }

        if (pageResponseDto.getPolicyList().isEmpty()) {
            return new BaseResponse<>(pageResponseDto, SUCCESS_POLICY_SEARCH_NO_RESULT);
        }
        return new BaseResponse<>(pageResponseDto, SUCCESS_POLICY_FOUND);
    }

    /**
     * 특정 정책 세부 조회
     */
    @GetMapping("/policies/{id}")
    public BaseResponse<PolicyDetailResponseDto> getPolicyDetail(@PathVariable Long id) {
        PolicyDetailResponseDto policyDetail = policyService.getPolicyDetail(id);
        return new BaseResponse<>(policyDetail, SUCCESS_POLICY_FOUND);
    }

    /**
     * 정책 스크랩 API
     */
    @PostMapping("/policies/{id}/scrap")
    public BaseResponse<String> scrap(@PathVariable Long id){
        if(policyService.scrapPolicy(id,memberService.getCurrentMember())!=null)
            return new BaseResponse<>(SUCCESS_SCRAP);
        else
            return new BaseResponse<>(SUCCESS_SCRAP_CANCEL);
    }

    /**
     * 스크랩한 정책 조회 API
     */
    @GetMapping("/policies/scrap")
    public BaseResponse<List<PolicyListResponseDto>> getMyScrapedPolicies(@PageableDefault(size = 10) Pageable pageable){
        List<PolicyListResponseDto> listResponseDto = policyService.getScrapPolicies(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(listResponseDto, SUCCESS);
    }

    /**
     * 정책 이름으로 조회
     */
    @GetMapping("/policies/search")
    public BaseResponse<List<SearchNameResponseDto>> getPoliciesByName(@RequestParam String title,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchNameResponseDto> listResponseDto = policyService.getPoliciesByName(title, pageable);
        if(listResponseDto.isEmpty())
            return new BaseResponse<>(listResponseDto, SUCCESS_POLICY_SEARCH_NO_RESULT);
        return new BaseResponse<>(listResponseDto, SUCCESS_POLICY_FOUND);
    }

    /**
     * 스크랩한 마감 임박 정책 조회 (최대 5개)
     */
    @GetMapping("/policies/scrapped/upcoming-deadline")
    public BaseResponse<List<PolicyListResponseDto>> getScrappedPoliciesWithUpcomingDeadline() {
        List<PolicyListResponseDto> listResponseDto = policyService.getScrappedPoliciesWithUpcomingDeadline(memberService.getCurrentMember());
        return new BaseResponse<>(listResponseDto, SUCCESS);
    }

    /**
     * 최근 본 정책 20개 조회
     */
    @GetMapping("/policies/recent-view")
    public BaseResponse<List<PolicyListResponseDto>> getRecentViewedPolicies() {
        List<PolicyListResponseDto> recentViewedPolicies = policyService.getRecentViewedPolicies();
        return new BaseResponse<>(recentViewedPolicies, SUCCESS);
    }

}
