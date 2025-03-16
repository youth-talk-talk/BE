package com.server.youthtalktalk.domain.policy.service.data;

import com.server.youthtalktalk.domain.policy.dto.data.PolicyData;
import com.server.youthtalktalk.domain.policy.entity.Policy;
import com.server.youthtalktalk.domain.policy.entity.region.PolicySubRegion;
import com.server.youthtalktalk.domain.policy.entity.region.Region;

import java.util.List;

public interface PolicyDataService {
    void saveData();
    List<PolicyData> fetchData();
    List<PolicySubRegion> setPolicySubRegions(Policy policy);
    Region searchRegionByZipCd(Policy policy);
    List<Policy> getPolicyEntityList(List<PolicyData> policyDataList);
}
