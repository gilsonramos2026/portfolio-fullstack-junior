package com.portifolio.api.entity.enums;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Formatos de imagem aceitos no upload. O tipo é identificado pelos primeiros bytes do arquivo
 * (assinatura), nunca pelo nome ou pelo Content-Type enviado pelo cliente. SVG fica de fora de propósito
 * (pode carregar scripts).
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ImageType {
    PNG("png", "image/png"),
    JPEG("jpg", "image/jpeg"),
    GIF("gif", "image/gif"),
    WEBP("webp", "image/webp");

    /** Quantidade de bytes iniciais necessária para identificar qualquer um dos formatos. */
    public static final int HEADER_SIZE = 12;

    private final String extension;
    private final String mediaType;

    public static Optional<ImageType> detect(byte[] header) {
        if (header == null) {
            return Optional.empty();
        }
        if (startsWith(header, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) {
            return Optional.of(PNG);
        }
        if (startsWith(header, 0xFF, 0xD8, 0xFF)) {
            return Optional.of(JPEG);
        }
        if (startsWith(header, 'G', 'I', 'F', '8')
                && header.length > 4 && (header[4] == '7' || header[4] == '9')
                && header.length > 5 && header[5] == 'a') {
            return Optional.of(GIF);
        }
        if (startsWith(header, 'R', 'I', 'F', 'F') && header.length >= 12
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return Optional.of(WEBP);
        }
        return Optional.empty();
    }

    public static Optional<ImageType> fromExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }
        for (ImageType type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    private static boolean startsWith(byte[] data, int... signature) {
        if (data.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((data[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }
}