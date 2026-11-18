package com.portifolio.api.dto.request;

import com.portifolio.api.entity.enums.EducationType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EducationRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        String title,

        @NotBlank(message = "O instituição/empresa é obrigatória")
        @Size(max = 120, message = "O instituição deve ter no máximo 120 caracteres")
        String institution,

        @NotNull(message = "O tipo é obrigatório")
        EducationType type,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String description,

        @NotNull(message = "A data de início é obrigatoria")
        LocalDate startDate,

        LocalDate endDate,

        @Size(max = 500, message = "A URL do certificado deve ter no máximo 500 caracteres")
        @Pattern(regexp = "^https?://.+", message = "A URL do certificado deve começar com http:// ou https://")
        String credentialUrl,

        @Min(value = 1, message = "A carga horária mínima é 1 hora")
        @Max(value = 10000, message = "A carga horária máxima e de 10000 horas")
        Integer workloadHours
) {
}
