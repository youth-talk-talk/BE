package com.server.youthtalktalk.dto.policy;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyDataListResponse {

    @JacksonXmlProperty(localName = "pageIndex")
    private int pageIndex;

    @JacksonXmlProperty(localName = "totalCnt")
    private int totalCnt;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "youthPolicy")
    private List<PolicyData> youthPolicies;
}
