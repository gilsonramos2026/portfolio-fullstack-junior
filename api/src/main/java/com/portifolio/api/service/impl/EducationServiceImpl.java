package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.EducationRequest;
import com.portifolio.api.dto.response.EducationResponse;
import com.portifolio.api.entity.Education;
import com.portifolio.api.entity.Project;
import com.portifolio.api.exception.BussinesRuleEducation;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.mapper.EducationMapper;
import com.portifolio.api.repository.EducationRepository;
import com.portifolio.api.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EducationServiceImpl implements EducationService {
    private static final String RESOURCE = "Formação";

    private final EducationRepository repository;
    private final EducationMapper mapper;

    @Override
    public List<EducationResponse> findAll() {
        return mapper.toResponseList(repository.findAllByOrderByStartDateDesc());
    }

    @Override
    public EducationResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public EducationResponse create(EducationRequest request) {
        validateDates(request);
        return mapper.toResponse((repository.save(mapper.toEntity(request))));
    }

    @Override
    @Transactional
    public EducationResponse update(Long id, EducationRequest request) {
        validateDates(request);
        Education education = getOrThrow(id);
        mapper.updateEntity(request, education);
        return mapper.toResponse((repository.saveAndFlush(education)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Education getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    private void validateDates(EducationRequest request) {
        if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
           throw new BussinesRuleEducation("A data de término não pode ser inferior a data de início");
        }
    }
}
