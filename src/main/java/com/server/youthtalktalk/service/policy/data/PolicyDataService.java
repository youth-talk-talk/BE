package com.server.youthtalktalk.service.policy.data;

import com.server.youthtalktalk.dto.policy.data.PolicyData;

import java.util.List;

public interface PolicyDataService {
    public void saveData();
    public List<PolicyData> fetchData();
}
