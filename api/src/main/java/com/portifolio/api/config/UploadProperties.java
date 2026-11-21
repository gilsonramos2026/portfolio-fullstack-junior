package com.portifolio.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(
        /** Pasta onde as imagens são gravadas (em produção, um volume persistente). */
        @NotBlank
        String dir,

        /** Tamanho máximo por imagem, em bytes. */
        @Positive
        long maxSizeBytes
) {
}