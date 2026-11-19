package com.portifolio.api.service.impl;

import com.portifolio.api.dto.request.ProfileRequest;
import com.portifolio.api.dto.response.ProfileResponse;
import com.portifolio.api.entity.Profile;
import com.portifolio.api.mapper.ProfileMapper;
import com.portifolio.api.repository.ProfileRepository;
import com.portifolio.api.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repository;
    private final ProfileMapper mapper;


    @Override
    public ProfileResponse get() {
        return mapper.toResponse(getOrCreate());
    }

    @Override
    @Transactional
    public ProfileResponse update(ProfileRequest request) {
        Profile profile = getOrCreate();
        mapper.updateEntity(request, profile);
        return mapper.toResponse(repository.saveAndFlush(profile));
    }

    /**Em banco novo não há perfil (o de exemplo so existe no perfil 'dev'):
     o primeiro acesso cria um em branco editavel no painel. */
    private Profile getOrCreate() {
        return repository.findFirstByOrderByIdAsc().orElseGet(() -> repository.save(defaltProfile()));
    }

    private static Profile defaltProfile() {
        Profile profile = new Profile();
        profile.setFullName("Seu nome");
        profile.setRole("Desenvolvedor Full-stack");
        profile.setHeadline("Construo aplicações we completas, do banco de dados a interface.");
        profile.setBio("Atualize sua bio no painel administrativo");
        profile.setAbout("Atualize o texto sobre você no painel administrativo.");
        profile.setEmail("voce@email.com");
        profile.setOpenWork(true);
        return profile;
    }
}
