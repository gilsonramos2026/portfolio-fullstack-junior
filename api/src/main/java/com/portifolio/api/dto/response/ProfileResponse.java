package com.portifolio.api.dto.response;

import com.portifolio.api.util.UrlPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileResponse(

        String fullName,
        String role,
        String headline,
        String bio,
        String about,
        String location,
        String email,
        String githubUrl,
        String linkedinUrl,
        String resumeUrl,
        String avatarUrl,
        Boolean openWork
) {
}
