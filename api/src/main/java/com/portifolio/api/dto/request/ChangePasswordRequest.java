package com.portifolio.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "Informe a senha atual")
        @Size(max = 100, message = "A senha atual deve ter no máximo 100 caracteres")
        String currentPassword,

        @NotBlank(message = "Informe a nova senha")
        @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres")
        String newPassword
) {
}
