package com.portifolio.api.mapper;

import com.portifolio.api.dto.request.EducationRequest;
import com.portifolio.api.dto.response.EducationResponse;
import com.portifolio.api.entity.Education;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEntity(EducationRequest request);

    void updateEntity(EducationRequest request, @MappingTarget Education education);

    @Mapping(target = "current", expression = "java(education.getEndDate() == null)")
    EducationResponse toResponse(Education education);

    List<EducationResponse> toResponseList(List<Education> education);
}