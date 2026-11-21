package com.portifolio.api.service;

import com.portifolio.api.dto.response.OrphanFilesResponse;

public interface UploadMaintenanceService {

    /** Imagens com mais de 24 h que nenhum registro referencia. Não altera nada. */
    OrphanFilesResponse findOrphans();

    /** Remove as imagens órfãs (as mesmas de {@link #findOrphans()}) e devolve o que foi removido. */
    OrphanFilesResponse deleteOrphans();
}
