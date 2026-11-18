package com.portifolio.api.util;

/** Expressões regulares compartilhadas pelos DTOs. */
public final class UrlPatterns {

    /** Im imagem: URL http(s) externa ou arquivo enviado pelo próprio painel (/api/files/<uuid>.ext). */
    public static final String IMAGE_URL =
            "^(https?://.+|/api/files/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(png|jpg|gif|webp))$";

    private UrlPatterns() {
    }
}
