package com.server.youthtalktalk.controller.policy;

import com.server.youthtalktalk.domain.policy.Category;
import com.server.youthtalktalk.dto.policy.PolicyListResponseDto;
import com.server.youthtalktalk.global.response.BaseResponse;
import com.server.youthtalktalk.global.response.BaseResponseCode;
import com.server.youthtalktalk.service.policy.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
     * 홈 화면 - 정책 조회 (top5 + allByCategory)
     */
    @GetMapping("/policies")
    public BaseResponse<Map<String, List<PolicyListResponseDto>>> getPoliciesByCategories(@RequestParam(required = false) List<Category> categories,
                                                                                          @RequestParam(defaultValue = "1") int page,
                                                                                          @RequestParam(defaultValue = "10") int size) {
        if (page < 1) {
            return new BaseResponse<>(INVALID_INPUT_VALUE);
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        List<PolicyListResponseDto> top5Policies = policyService.getTop5Policies(); // 모든 정책에 조회수가 하나도 없으면 빈 리스트 반환

        List<PolicyListResponseDto> allPolicies = policyService.getPoliciesByCategories(categories, pageable);

        Map<String, List<PolicyListResponseDto>> responseMap = new LinkedHashMap<>();
        responseMap.put("top5Policies", top5Policies);
        responseMap.put("allPolicies", allPolicies);

        return new BaseResponse<>(responseMap, BaseResponseCode.SUCCESS);
    }





}
