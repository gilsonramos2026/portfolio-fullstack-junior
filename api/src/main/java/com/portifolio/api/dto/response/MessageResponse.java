package com.portifolio.api.dto.response;

import com.portifolio.api.entity.enums.MessageSource;

import java.time.Instant;

public record MessageResponse(
    Long id,
    String name,
    String email,
    String message,
    MessageSource source,
    boolean read,
    Instant CreatedAt
) {
}
