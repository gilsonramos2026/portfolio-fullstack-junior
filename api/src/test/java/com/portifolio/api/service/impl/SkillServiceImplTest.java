package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.SkillRequest;
import com.portifolio.api.entity.Skill;
import com.portifolio.api.entity.enums.SkillCategory;
import com.portifolio.api.exception.DuplicateResourceException;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.mapper.SkillMapper;
import com.portifolio.api.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private SkillRepository repository;

    private SkillServiceImpl service;

    private final SkillRequest request = new SkillRequest("Java", SkillCategory.BACKEND, 4, null);

    @BeforeEach
    void setUp() {
        service = new SkillServiceImpl(repository, Mappers.getMapper(SkillMapper.class));
    }

    @Test
    void createMapsRequestToResponse() {
        when(repository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(repository.save(any(Skill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertThat(response.name()).isEqualTo("Java");
        assertThat(response.category()).isEqualTo(SkillCategory.BACKEND);
        assertThat(response.level()).isEqualTo(4);
    }

    @Test
    void CreateRejectsDuplicateName() {
        when(repository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(DuplicateResourceException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void updateRejectsNameUsedByAnotherSkill(){
        when(repository.findById(1L)).thenReturn(Optional.of(new Skill()));
        when(repository.existsByNameIgnoreCaseAndIdNot("Java", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1L,request)).isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(5L)).isInstanceOf(ResourceNotFoundException.class);
    }
}