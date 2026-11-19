package com.portifolio.api.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        @NotBlank
        String jwtSecret,

        @Min(1)
        long jwtExpirationMinutes,

        List<String> allowedOrigins
) {
    public SecurityProperties {
        allowedOrigins = allowedOrigins == null
                ? List.of()
                : allowedOrigins.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}