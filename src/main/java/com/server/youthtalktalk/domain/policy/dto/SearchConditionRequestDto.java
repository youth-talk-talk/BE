package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.Category;
import com.server.youthtalktalk.domain.policy.entity.EmploymentCode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchConditionRequestDto {
    private List<Category> categories; // 카테고리
    private Integer age; // 연령
    private List<EmploymentCode> employmentCodeList; // 취업상태
    private Boolean isFinished; // 마감여부
    private String keyword; // 검색 키워드


}
