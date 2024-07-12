package com.server.youthtalktalk.controller.policy;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.dto.policy.PolicyDetailResponseDto;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.policy.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.server.youthtalktalk.global.response.BaseResponseCode.INVALID_INPUT_VALUE;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PolicyController {
    private final PolicyService policyService;

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

        return new BaseResponse<>(responseMap, BaseResponseCode.SUCCESS);
    }

    /**
     * 특정 정책 세부 조회
     */
    @GetMapping("/policies/{id}")
    public BaseResponse<PolicyDetailResponseDto> getPolicyDetail(@PathVariable String id) {
        PolicyDetailResponseDto policyDetail = policyService.getPolicyDetail(id);
        return new BaseResponse<>(policyDetail, BaseResponseCode.SUCCESS);
    }





}
