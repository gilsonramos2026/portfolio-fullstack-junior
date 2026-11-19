package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.ProfileRequest;
import com.portifolio.api.entity.Profile;
import com.portifolio.api.mapper.ProfileMapper;
import com.portifolio.api.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository repository;

    private ProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProfileServiceImpl(repository, Mappers.getMapper(ProfileMapper.class));
    }

    @Test
    void getCreatesDefaultProfileWhenMissing() {
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());
        when(repository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.get();

        assertThat(response.fullName()).isEqualTo("Seu nome");
        assertThat(response.openWork()).isTrue();
        verify(repository).save(any(Profile.class));
    }

    @Test
    void updateOverwritesFieldsOfExistingProfile() {
        Profile existing = new Profile();
        when(repository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);

        var request = new ProfileRequest(
                "Ana Sousa",
                "Dev Backend Júnior",
                "Headline",
                "Bio",
                "Sobre",
                "São Paulo",
                "ana@email.com",
                null,
                null,
                null,
                "https://img.example/ana.png",
                false
        );

        var response = service.update(request);

        assertThat(response.fullName()).isEqualTo("Ana Sousa");
        assertThat(response.avatarUrl()).isEqualTo("https://img.example/ana.png");
        assertThat(response.openWork()).isFalse();
    }
}