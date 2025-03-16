package com.server.youthtalktalk.domain.policy.dto;

import com.server.youthtalktalk.domain.policy.entity.InstitutionType;
import com.server.youthtalktalk.domain.policy.entity.SubCategory;
import com.server.youthtalktalk.domain.policy.entity.condition.Education;
import com.server.youthtalktalk.domain.policy.entity.condition.Employment;
import com.server.youthtalktalk.domain.policy.entity.condition.Major;
import com.server.youthtalktalk.domain.policy.entity.condition.Specialization;
import com.server.youthtalktalk.domain.policy.entity.region.SubRegion;
import com.server.youthtalktalk.domain.policy.entity.condition.Marriage;
import java.util.List;
import lombok.Builder;

@Builder
public record SearchConditionDto(
        String keyword,
        InstitutionType institutionType,
        List<SubCategory> subCategories,
        Marriage marriage,
        Integer age,
        Integer minEarn,
        Integer maxEarn,
        List<Education> educations,
        List<Major> majors,
        List<Employment> employments,
        List<Specialization> specializations,
        List<Long> subRegionIds,
        Boolean isFinished
        ) {
}
