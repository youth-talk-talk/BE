package com.server.youthtalktalk.controller.policy.data;

import com.server.youthtalktalk.service.policy.data.PolicyDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PolicyDataController {
    private final PolicyDataService policyDataService;

    @GetMapping("/data/fetch")
    public String fetchData() {
        log.info("fetch data");
        policyDataService.saveData();
        return "success!";
    }
}
