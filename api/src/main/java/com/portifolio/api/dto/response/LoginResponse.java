package com.portifolio.api.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        String username
) {
}
