package com.portifolio.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Proxies reversos em que a API confia para informar o IP real do visitante (cabeçalho X-Forwarded-For).
 * Aceita IPs e faixas CIDR. Lista vazia = não confiar em nenhum cabeçalho.
 */
@ConfigurationProperties(prefix = "app.proxy")
public record ProxyProperties(
        List<String> trustedProxies
) {
    public ProxyProperties {
        trustedProxies = trustedProxies == null
                ? List.of()
                : trustedProxies.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}