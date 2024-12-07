package com.server.youthtalktalk.domain.policy.service.data;

import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;

import java.util.List;

public interface PolicyDataService {
    public void saveData();
    public List<PolicyData> fetchData();
}
