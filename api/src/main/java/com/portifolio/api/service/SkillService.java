package com.portifolio.api.service;

import com.portifolio.api.dto.request.SkillRequest;
import com.portifolio.api.dto.response.SkillResponse;
import com.portifolio.api.entity.enums.SkillCategory;

import java.util.List;

public interface SkillService extends CrudService<SkillRequest, SkillResponse> {

    List<SkillResponse> findByCategory(SkillCategory category);
}
