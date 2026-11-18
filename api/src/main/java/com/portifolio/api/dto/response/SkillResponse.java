package com.portifolio.api.dto.response;

import com.portifolio.api.entity.enums.SkillCategory;

public record SkillResponse (
  Long id,
  String name,
  SkillCategory category,
  int level,
  String iconUrl
){}
