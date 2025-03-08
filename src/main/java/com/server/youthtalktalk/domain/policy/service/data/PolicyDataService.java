package com.server.youthtalktalk.domain.policy.service.data;

import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;
import com.server.youthtalktalk.domain.policy.entity.Policy;

import java.util.List;

public interface PolicyDataService {
    void saveData();
    List<PolicyData> fetchData();
}
