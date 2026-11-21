package com.portifolio.api.dto.request;

import com.portifolio.api.entity.enums.MessageSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        @Schema(example = "Ana Souza")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres")
        @Schema(example = "ana@empresa.com")
        String email,

        @NotBlank(message = "A mensagem é obrigatória")
        @Size(min = 10, max = 2000, message = "A mensagem deve ter entre 10 e 2000 caracteres")
        @Schema(example = "Olá! Gostei do seu portfólio e gostaria de conversar sobre uma vaga")
        String message,

        @Schema(defaultValue = "FORM")
        MessageSource source,

        /** Campo isca (honeypot): invisível para humanos; se vier preenchido, a mensagem é descartada */
        @Size(max = 200)
        @Schema(description = "Campo isca anti-spam: deixe vazio. Se vier preenchido, o recado é descartado")
        String website
) {
}