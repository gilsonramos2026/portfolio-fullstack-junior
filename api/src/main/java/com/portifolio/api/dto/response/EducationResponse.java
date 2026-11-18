package com.portifolio.api.dto.response;

import com.portifolio.api.entity.enums.EducationType;

import java.time.LocalDate;

public record EducationResponse(
        Long id,
        String title,
        String institution,
        EducationType type,
        LocalDate startDate,
        LocalDate endDate,
        String credentialUrl,
        Integer workloadHours,
        boolean current
) {
}
