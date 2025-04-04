package com.server.youthtalktalk.domain.policy.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "StanOrgCd")
public record DepartmentResponseDto(@JacksonXmlProperty(localName = "head") Head head,
                                    @JacksonXmlElementWrapper(useWrapping = false)
                                    @JacksonXmlProperty(localName = "row") List<Row> row) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Head(
            @JacksonXmlProperty(localName = "totalCount") Integer totalCount,
            @JacksonXmlProperty(localName = "numOfRows") String numOfRows,
            @JacksonXmlProperty(localName = "pageNo") String pageNo,
            @JacksonXmlProperty(localName = "type") String type
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Row(
            @JacksonXmlProperty(localName = "org_cd") String orgCd,
            @JacksonXmlProperty(localName = "full_nm") String fullNm,
            @JacksonXmlProperty(localName = "highst_cd") String highstCd,
            @JacksonXmlProperty(localName = "rep_cd") String repCd
    ) {}
}
