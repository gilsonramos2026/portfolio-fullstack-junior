package com.portifolio.api.mapper;

import com.portifolio.api.dto.request.SkillRequest;
import com.portifolio.api.dto.response.SkillResponse;
import com.portifolio.api.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    Skill toEntity(SkillRequest request);

    void updateEntity(SkillRequest request, @MappingTarget Skill skill);

    SkillResponse toResponse(Skill skill);

    List<SkillResponse> toResponseList(List<Skill> skills);
}
