package com.portifolio.api.dto.request;

import com.portifolio.api.util.UrlPatterns;

import jakarta.validation.constraints.*;

import java.util.List;

public record ProfileRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String fullName,

        @NotBlank(message = "O cargo/título é obrigatório")
        @Size(max = 120, message = "O cargo deve ter no máximo 120 caracteres")
        String role,

        @NotBlank(message = "O resumo curto é obrigatório")
        @Size(max = 300, message = "O nome deve ter no maximo 300 caracteres")
        String headline,

        @NotBlank(message = "A bio é obrigatória")
        @Size(max = 1000, message = "A bio deve ter no maximo 1000 caracteres")
        String bio,

        @NotBlank(message = "O texto 'sobre mim' é obrigatório")
        @Size(max = 3000, message = "O texto 'sobre mim' deve ter no maximo 3000 caracteres")
        String about,

        @Size(max = 120, message = "A localização deve ter no maximo 120 caracteres")
        String location,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 160, message = "O texto email deve ter no maximo 160 caracteres")
        String email,

        @Size(max = 500,  message = "A URL do github deve ter no maximo 500 caracteres")
        @Pattern(regexp = "^https?://.+",message = "A URL do github deve começar com http:// ou https://")
        String githubUrl,

        @Size(max = 500,  message = "A URL do linkedin deve ter no maximo 500 caracteres")
        @Pattern(regexp = "^https?://.+",message = "A URL do linkedin deve começar com http:// ou https://")
        String linkedinUrl,

        @Size(max = 500,  message = "A URL do curriculo deve ter no maximo 500 caracteres")
        @Pattern(regexp = "^https?://.+",message = "A URL do curriculo deve começar com http:// ou https://")
        String resumeUrl,

        @Size(max = 500,  message = "A URL da foto deve ter no maximo 500 caracteres")
        @Pattern(regexp = UrlPatterns.IMAGE_URL,message = "A foto deve ser uma URL http(s):// ou uma imagem enviada " +
                "pelo painel")
        String avatarUrl,

        Boolean openWork
) {
}
