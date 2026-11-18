package com.portifolio.api.mapper;

import com.portifolio.api.dto.request.ProjectRequest;
import com.portifolio.api.dto.response.ProjectResponse;
import com.portifolio.api.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toEntity(ProjectRequest request);

    void updateEntity(ProjectRequest request, @MappingTarget Project project);

    ProjectResponse toResponse(Project project);

    List<ProjectResponse> toResponseList(List<Project> projects);
}
