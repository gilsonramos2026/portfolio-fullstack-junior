package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.ProjectRequest;
import com.portifolio.api.dto.response.ProjectResponse;
import com.portifolio.api.entity.Project;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.mapper.ProjectMapper;
import com.portifolio.api.repository.ProjectRepository;
import com.portifolio.api.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final String RESOURCE = "Projeto";

    private final ProjectRepository repository;
    private final ProjectMapper mapper;

    @Override
    public List<ProjectResponse> findAll() {
        return mapper.toResponseList((repository.findAllByOrderByFeaturedDescCreatedAtDesc()));
    }

    @Override
    public ProjectResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getOrThrow(id);
        mapper.updateEntity(request, project);
        return mapper.toResponse(repository.saveAndFlush(project));
    }

    @Override
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Project getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }
}
