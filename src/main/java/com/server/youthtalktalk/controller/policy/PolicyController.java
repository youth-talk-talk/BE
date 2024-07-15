package com.server.youthtalktalk.controller.policy;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.dto.policy.SearchConditionRequestDto;
import com.server.youthtalktalk.dto.policy.PolicyDetailResponseDto;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.member.MemberService;
import com.server.youthtalktalk.service.policy.PolicyService;
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
     * 홈 화면 정책 조회 (top5 + allByCategory)
     */
    @GetMapping("/policies")
    public BaseResponse<Map<String, List<PolicyListResponseDto>>> getTop5AndCategoryPolicies(@RequestParam List<Category> categories,
                                                                                             @PageableDefault(size = 10) Pageable pageable) {

        List<PolicyListResponseDto> top5Policies = policyService.getTop5Policies();
        List<PolicyListResponseDto> allPolicies = policyService.getPoliciesByCategories(categories, pageable);

        Map<String, List<PolicyListResponseDto>> responseMap = new LinkedHashMap<>();
        responseMap.put("top5Policies", top5Policies);
        responseMap.put("allPolicies", allPolicies);

        return new BaseResponse<>(responseMap, BaseResponseCode.SUCCESS_POLICY_FOUND);
    }

    /**
     * 특정 정책 세부 조회
     */
    @GetMapping("/policies/{id}")
    public BaseResponse<PolicyDetailResponseDto> getPolicyDetail(@PathVariable String id) {
        PolicyDetailResponseDto policyDetail = policyService.getPolicyDetail(id);
        return new BaseResponse<>(policyDetail, BaseResponseCode.SUCCESS_POLICY_FOUND);
    }

    /**
     * 정책 스크랩 API
     */
    @PostMapping("/policies/{id}/scrap")
    public BaseResponse<String> scrap(@PathVariable String id){
        if(policyService.scrapPolicy(id,memberService.getCurrentMember())!=null)
            return new BaseResponse<>(BaseResponseCode.SUCCESS_SCRAP);
        else
            return new BaseResponse<>(BaseResponseCode.SUCCESS_SCRAP_CANCEL);
    }

    /**
     * 스크랩한 정책 조회 API
     */
    @GetMapping("/policies/scrap")
    public BaseResponse<List<PolicyListResponseDto>> getMyScrapedPolicies(@PageableDefault(size = 10) Pageable pageable){
        List<PolicyListResponseDto> listResponseDto = policyService.getScrapPolicies(pageable,memberService.getCurrentMember());
        return new BaseResponse<>(listResponseDto, BaseResponseCode.SUCCESS);
    }

    /**
     * 조건 적용 정책 조회
     */
    @PostMapping("/policies/search")
    public BaseResponse<List<PolicyListResponseDto>> getPoliciesByCondition(@RequestBody SearchConditionRequestDto request,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, size);
        List<PolicyListResponseDto> policies = policyService.getPoliciesByCondition(request, pageable);
        if(policies.isEmpty())
            return new BaseResponse<>(policies, BaseResponseCode.SUCCESS_POLICY_SEARCH_NO_RESULT);
        return new BaseResponse<>(policies, BaseResponseCode.SUCCESS_POLICY_FOUND);
    }




}
