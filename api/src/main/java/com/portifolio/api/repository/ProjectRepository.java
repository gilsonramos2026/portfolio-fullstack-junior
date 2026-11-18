package com.portifolio.api.repository;

import com.portifolio.api.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderByFeaturedDescCreatedAtDesc();

    /** Capas de projeto em uso (para detectar uploads órfãos) */
    @Query("select p.imageUrl from Project p where p.imageUrl is not null") // Corrigido 'project' para 'Project'
    List<String> findAllImageUrls();

    /** Capturas de tela em uso (para detectar uploads órfãos) */
    @Query("select s from Project p join p.screenshots s")
    List<String> findAllScreenshotUrls();
}