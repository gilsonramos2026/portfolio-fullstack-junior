package com.portifolio.api.repository;

import com.portifolio.api.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findAllByOrderByCategoryAscLevelDescNameAsc();

    List<Skill> findAllByCategoryOrderByLevelDescNameAsc(String category);

    boolean existsByNameIgnoreCase(String name);

    @Query("select s.iconUrl from Skill s where s.iconUrl is not null")
    List<String> findAllIconUrls();

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
