package com.server.youthtalktalk.domain.policy.controller;

import static com.server.youthtalktalk.global.response.BaseResponseCode.*;

import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.policy.dto.*;
import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.SortOption;
import com.server.youthtalktalk.domain.policy.entity.region.Region;
import com.server.youthtalktalk.domain.policy.service.PolicyService;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.domain.policy.service.PolicyService;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;
    private final MemberService memberService;

    /**
     * 홈 화면 정책 조회 (우리 지역 인기 정책 + 따끈따끈 새로운 정책)
     */
    @GetMapping("/policies")
    public BaseResponse<Map<String, List<PolicyListResponseDto>>> getHomePolicies(@RequestParam(required = false) List<Category> categories) {
        List<PolicyListResponseDto> top20Policies = policyService.getTop20Policies();
        List<PolicyListResponseDto> newPolicies = policyService.getNewPoliciesByCategories(categories);

        Map<String, List<PolicyListResponseDto>> responseMap = new LinkedHashMap<>();
        responseMap.put("top20Policies", top20Policies);
        responseMap.put("newPolicies", newPolicies);

        return new BaseResponse<>(responseMap, SUCCESS_POLICY_FOUND);
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
     * 조건 적용 정책 조회
     */
    @PostMapping("/policies/search")
    public BaseResponse<SearchConditionResponseDto> getPoliciesByCondition(
            @RequestBody SearchConditionRequestDto request,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "RECENT") SortOption sort) {

        Pageable pageable = PageRequest.of(page, size);
        SearchConditionResponseDto listResponseDto = policyService.getPoliciesByCondition(request, pageable, sort);
        if (listResponseDto.getPolicyList().isEmpty()) {
            return new BaseResponse<>(listResponseDto, SUCCESS_POLICY_SEARCH_NO_RESULT);
        }
        return new BaseResponse<>(listResponseDto, SUCCESS_POLICY_FOUND);
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
     * 조회수 top5 정책 조회 (최대 5개, 정책별로 후기게시글 같이 반환)
     */
    @GetMapping("/policies/top5-with-reviews")
    public BaseResponse<List<PolicyWithReviewsDto>> getTop5PoliciesWithReviews() {
        List<PolicyWithReviewsDto> top5PoliciesWithReviews = policyService.getTop5PoliciesWithReviews(memberService.getCurrentMember());
        return new BaseResponse<>(top5PoliciesWithReviews, SUCCESS);
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
