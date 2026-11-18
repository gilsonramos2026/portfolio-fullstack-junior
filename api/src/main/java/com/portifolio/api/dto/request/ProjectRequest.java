package com.portifolio.api.dto.request;

import com.portifolio.api.util.UrlPatterns;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProjectRequest(

        @NotBlank(message = "O titulo é obrigatório")
        @Size(max = 120, message = "O titulo deve ter no máximo 120 caracteres")
        String title,

        @NotBlank(message = "A descrição é obrigatório")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description,

        @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres")
        @Pattern(regexp = UrlPatterns.IMAGE_URL, message = "A imagem de capa deve ser uma URL http(s):// ou uma imagem enviada pelo painel")
        String imageUrl,

        @Size(max = 500, message = "A URL do github deve ter no máximo 500 caracteres")
        @Pattern(regexp = "^https?://.+", message = "A URL do github deve começar com http:// ou https://")
        String githubUrl,

        @Size(max = 500, message = "A URL do deploy deve ter no máximo 500 caracteres")
        @Pattern(regexp = "^https?://.+", message = "A URL do deploy deve começar com http:// ou https://")
        String deployUrl,

        @Size(max = 3000, message = "A descrição detalhada deve ter no máximo 3000 caracteres")
        String longDescription,

        boolean featured,

        @NotNull(message = "A lista de tecnologias é obrigatória")
        @Size(max = 20, message = "Informe no máximo 20 tecnologias")
        List<@NotBlank(message = "Tecnologia não pode ser vazia")
        @Size(max = 60, message = "Cada tecnologia deve ter no máximo 60 caracteres") String>
        technologies,

        @NotNull(message = "A lista de funcionalidades é obrigatória")
        @Size(max = 12, message = "Informe no máximo 12 funcionalidades")
        List<@NotBlank(message = "Funcionalidades não pode ser vazia")
        @Size(max = 200, message = "Cada funcionalidade deve ter no máximo 200 caracteres") String>
        features,

        @NotNull(message = "A lista de capituras de tela é obrigatória")
        @Size(max = 8, message = "Informe no máximo 8 capturas de tela")
        List<@NotBlank(message = "A URL da captura não pode ser vazia")
        @Size(max = 500, message = "Cada URL deve ter no máximo 500 caracteres")
        @Pattern(regexp = UrlPatterns.IMAGE_URL, message = "As capturas devem ser URLs http(s):// ou imagens enviadas pelo painel") String>
        screenshots
) {
}