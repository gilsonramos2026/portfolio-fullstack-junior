package com.portifolio.api.dto.request;

import com.portifolio.api.entity.enums.SkillCategory;
import com.portifolio.api.util.UrlPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record SkillRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        @Schema(example = "Spring Boot")
        String name,

        @NotNull(message = "A categoria é obrigatória")
        SkillCategory category,

        @NotNull(message = "O nível é obrigatório")
        @Min(value = 1, message = "O nível mínimo é 1")
        @Max(value = 5, message = "O nivel máximo é 5")
        @Schema(example = "4",description = "De 1 (básico) a 5 (avançado)")
        Integer level,

        @Size(max = 500, message = "A URL do ícone deve ter no máximo 500 caracteres")
        @Pattern(regexp = UrlPatterns.IMAGE_URL,message = "ícone deve ser uma URL http(s):// ou uma imagem enviada pelo painel")
        @Schema(example = "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg",
            description = "URL http(s):// ou imagem enviada (/api/files/<arquivo>)")
        String iconUrl
) {
}
