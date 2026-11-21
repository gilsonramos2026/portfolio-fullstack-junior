package com.portifolio.api.service.impl;

import com.portifolio.api.config.UploadProperties;
import com.portifolio.api.entity.enums.ImageType;
import com.portifolio.api.exception.BusinessRuleException;
import com.portifolio.api.exception.ResourceNotFoundException;
import com.portifolio.api.service.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Grava as imagens em disco com nome aleatório (UUID) e extensão decidida pelo conteúdo real do arquivo.
 * O nome original enviado pelo cliente nunca é usado, o que elimina path traversal e colisões.
 * JPEG e PNG são regravados sem metadados e com dimensões limitadas.
 */
@Service
public class LocalFileStorageService implements FileStorageService {

    public static final String URL_PREFIX = "/api/files/";
    private static final Pattern STORED_NAME =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(png|jpg|gif|webp)$");

    private final ImageSanitizer sanitizer = new ImageSanitizer();
    private final Path root;
    private final long maxSizeBytes;

    public LocalFileStorageService(UploadProperties properties) {
        this.root = Path.of(properties.dir()).toAbsolutePath().normalize();
        this.maxSizeBytes = properties.maxSizeBytes();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar a pasta de uploads: " + root, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Selecione uma imagem para enviar");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new BusinessRuleException("A imagem deve ter no máximo " + (maxSizeBytes / (1024 * 1024)) + " MB");
        }
        try {
            byte[] content = file.getBytes();
            ImageType type = ImageType.detect(Arrays.copyOf(content, Math.min(content.length, ImageType.HEADER_SIZE)))
                    .orElseThrow(() -> new BusinessRuleException("Formato não suportado. Envie PNG, JPEG, WebP ou GIF."));
            // JPEG/PNG são reprocessados: sem EXIF/metadados, com dimensões limitadas (ver ImageSanitizer).
            byte[] clean = sanitizer.sanitize(content, type);
            String filename = UUID.randomUUID() + "." + type.extension();
            Files.write(root.resolve(filename), clean, StandardOpenOption.CREATE_NEW);
            return URL_PREFIX + filename;
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar a imagem", e);
        }
    }

    @Override
    public List<StoredFileInfo> listAll() {
        List<StoredFileInfo> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(root)) {
            for (Path path : (Iterable<Path>) files::iterator) {
                if (!Files.isRegularFile(path)) {
                    continue;
                }
                String name = path.getFileName().toString();
                if (STORED_NAME.matcher(name).matches()) {
                    result.add(new StoredFileInfo(name, Files.size(path), Files.getLastModifiedTime(path).toInstant()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao listar a pasta de uploads", e);
        }
        return result;
    }

    @Override
    public boolean delete(String filename) {
        if (filename == null || !STORED_NAME.matcher(filename).matches()) {
            return false;
        }
        Path path = root.resolve(filename).normalize();
        if (!path.startsWith(root)) {
            return false;
        }
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover a imagem", e);
        }
    }

    @Override
    public StoredFile load(String filename) {
        if (filename == null || !STORED_NAME.matcher(filename).matches()) {
            throw new ResourceNotFoundException("Imagem não encontrada");
        }
        Path path = root.resolve(filename).normalize();
        if (!path.startsWith(root) || !Files.isReadable(path)) {
            throw new ResourceNotFoundException("Imagem não encontrada");
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1);
        ImageType type = ImageType.fromExtension(extension)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada"));
        return new StoredFile(new FileSystemResource(path), type);
    }
}