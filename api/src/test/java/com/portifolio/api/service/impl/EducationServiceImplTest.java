package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.EducationRequest;
import com.portifolio.api.entity.Education;
import com.portifolio.api.entity.enums.EducationType;
import com.portifolio.api.exception.BussinesRuleEducation;
import com.portifolio.api.mapper.EducationMapper;
import com.portifolio.api.repository.EducationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceImplTest {

    @Mock
    private EducationRepository repository;

    private EducationServiceImpl service;

    @BeforeEach
    void setUp() {
        EducationMapper mapper = Mappers.getMapper(EducationMapper.class);
        service = new EducationServiceImpl(repository, mapper);
    }

    @Test
    void createRejectsEndDateBeforeStartDate() {
        var request = new EducationRequest(
                "Curso",
                "Universidade",
                EducationType.DEGREE,
                null,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 1, 1),
                null,
                null
        );

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BussinesRuleEducation.class);

        verify(repository, never()).save(any());
    }

    @Test
    void ongoingEducationIsMarkedAsCurrent() {
        var request = new EducationRequest(
                "Curso",
                "Universidade",
                EducationType.DEGREE,
                null,
                LocalDate.of(2025, 5, 1),
                null,
                null,
                null
        );

        when(repository.save(any(Education.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.create(request).current()).isTrue();
    }
}