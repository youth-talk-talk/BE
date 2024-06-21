package com.server.youthtalktalk.domain.policy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepeatCode {
    ALWAYS("002001","상시"),
    ANNUALLY("002002","연간반복"),
    MONTHLY("002003","월간반복"),
    PERIOD("002004","특정기간"),
    UNDEFINED("002005","미정");

    private final String key;
    private final String name;
}
