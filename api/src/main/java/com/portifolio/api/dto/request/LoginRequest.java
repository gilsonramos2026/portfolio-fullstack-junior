package com.portifolio.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "O usuário é obrigatório")
        @Size(max = 60, message = "O usuário deve ter mais de 60 caracteres")
        @Schema(example = "admin")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        @Size(max = 100, message = "A senha deve ter no máximo 100 caracteres")
        @Schema(example = "sua-senha-de-admin")
        String password
) {
}
