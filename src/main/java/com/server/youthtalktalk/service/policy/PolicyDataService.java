package com.server.youthtalktalk.service.policy;

import com.server.youthtalktalk.dto.policy.PolicyData;

import java.util.List;

public interface PolicyDataService {
    public void saveData();
    public List<PolicyData> fetchData();
}
