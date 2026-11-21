package com.portifolio.api.dto.response;

import java.util.List;

/** Imagens enviadas que nenhum projeto, skill ou perfil usa mais. */
public record OrphanFilesResponse(
        int count,
        long totalBytes,
        List<String> files
) {
}
