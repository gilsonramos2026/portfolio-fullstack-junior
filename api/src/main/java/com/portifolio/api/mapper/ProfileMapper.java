package com.portifolio.api.mapper;

import com.portifolio.api.dto.request.ProfileRequest;
import com.portifolio.api.dto.response.ProfileResponse;
import com.portifolio.api.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    void updateEntity(ProfileRequest request, @MappingTarget Profile profile);

    ProfileResponse toResponse(Profile profile);
}