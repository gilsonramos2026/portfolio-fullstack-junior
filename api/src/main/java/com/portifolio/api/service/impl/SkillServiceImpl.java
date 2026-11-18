package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.SkillRequest;
import com.portifolio.api.dto.response.SkillResponse;
import com.portifolio.api.entity.Skill;
import com.portifolio.api.enums.SkillCategory;
import com.portifolio.api.exception.DuplicateResourceException;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.mapper.SkillMapper;
import com.portifolio.api.repository.SkillRepository;
import com.portifolio.api.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{

    private static final String RESOURCE = "Skill";

    private final SkillRepository repository;
    private final SkillMapper mapper;

    @Override
    public List<SkillResponse> findByCategory(SkillCategory category) {
        return mapper.toResponseList(repository.findAllByCategoryOrderByLevelDescNameAsc(category));
    }

    @Override
    public List<SkillResponse> findAll() {
        return mapper.toResponseList(repository.findAllByOrderByCategoryAscLevelDescNameAsc());
    }

    @Override
    public SkillResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public SkillResponse create (SkillRequest request) {
        if (repository.existsByNameIgnoreCase(request.name().trim())) {
            throw duplicate(request.name());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public SkillResponse update(Long id, SkillRequest request) {
        Skill skill = getOrThrow(id);
        if (repository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), id)) {
            throw duplicate(request.name());
        }
        mapper.updateEntity(request, skill);
        return mapper.toResponse(repository.saveAndFlush(skill));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Skill getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    private DuplicateResourceException duplicate(String name) {
        return new DuplicateResourceException("Já existe uma skill chamada '%s'".formatted(name.trim()));
    }
}
