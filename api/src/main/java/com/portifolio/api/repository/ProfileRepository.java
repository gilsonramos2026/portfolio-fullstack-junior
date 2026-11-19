package com.portifolio.api.repository;

import com.portifolio.api.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findFirstByOrderByIdAsc();

    /** Fotos de perfil em uso (para detectar uploads órfãos) */
    @Query("select p.avatarUrl from Profile p where p.avatarUrl is not null")
    List<String> findAllAvatarUrl();
}