package com.portifolio.api.repository;

import com.portifolio.api.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    List<Education> findAllByOrderByStartDateDesc();
}
