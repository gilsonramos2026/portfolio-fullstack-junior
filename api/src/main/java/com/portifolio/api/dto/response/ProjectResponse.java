package com.portifolio.api.dto.response;

import java.time.Instant;
import java.util.List;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        String imageUrl,
        String githubUrl,
        String deployUrl,
        String longDescription,
        boolean featured,
        List<String> technologies,
        List<String> features,
        List<String> screenshots,
        Instant createdAt,
        Instant updatedAt
){
}
