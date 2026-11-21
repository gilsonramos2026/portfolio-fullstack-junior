package com.portifolio.api.service;

import com.portifolio.api.entity.enums.ImageType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface FileStorageService {

    /** Valida e grava a imagem; devolve o caminho público, ex.: {@code /api/files/<uuid>.png}. */
    String store(MultipartFile file);

    /** Localiza uma imagem já enviada; lança ResourceNotFoundException se não existir. */
    StoredFile load(String filename);

    /** Todas as imagens guardadas, com tamanho e data de gravação. */
    List<StoredFileInfo> listAll();

    /** Remove uma imagem pelo nome gravado (ex.: {@code <uuid>.png}); devolve false se ela não existir. */
    boolean delete(String filename);

    record StoredFileInfo(String filename, long sizeBytes, Instant lastModified) {
    }

    record StoredFile(Resource resource, ImageType type) {
    }
}