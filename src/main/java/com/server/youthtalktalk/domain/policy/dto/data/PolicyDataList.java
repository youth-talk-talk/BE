package com.server.youthtalktalk.domain.policy.dto.data;

import java.util.List;

public record PolicyDataList(int resultCode, String resultMessage, Result result) {

    public record Result(Pagging pagging, List<PolicyData> youthPolicyList) {}

    public record Pagging(int totCount, int pageNum, int pageSize) {}// 예시 필드 추가
}

