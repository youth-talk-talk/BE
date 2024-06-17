package com.server.youthtalktalk.domain.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    JOB("일자리"),
    EDUCATION("교육"),
    LIFE("생활지원"),
    PARTICIPATION("참여");

    private final String key;
}
