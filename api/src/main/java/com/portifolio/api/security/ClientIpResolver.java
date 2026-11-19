package com.portifolio.api.security;

import com.portifolio.api.config.ProxyProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Descobre o IP do visitante para os limites de tentativas (login e recados).
 *
 * <p>O cabeçalho {@code X-Forwarded-For} só é considerado quando a conexão vem de um proxy confiável
 * (o servidor Next.js ou um proxy reverso na rede privada). Para qualquer outra origem, vale o IP da
 * própria conexão, o que impede que um cliente qualquer forje o cabeçalho para burlar os limites.
 */
@Component
public class ClientIpResolver {

    private static final String FORWARDED_FOR = "X-Forwarded-For";

    /** Apenas caracteres de IPv4/IPv6 e tamanho máximo de um IPv6 textual (evita lixo como chave e em logs). */
    private static final Pattern IP_LITERAL = Pattern.compile("^[0-9a-fA-F:.]{2,45}$");

    private final List<IpAddressMatcher> trustedProxies;

    public ClientIpResolver(ProxyProperties properties) {
        this.trustedProxies = properties.trustedProxies().stream()
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .map(IpAddressMatcher::new)
                .toList();
    }

    public String resolve(HttpServletRequest request) {
        String peer = request.getRemoteAddr();
        String forwarded = request.getHeader(FORWARDED_FOR);
        if (forwarded == null || forwarded.isBlank() || !isTrusted(peer)) {
            return peer;
        }
        // O Next.js envia um único valor, já validado por ele; em cadeias maiores vale o primeiro item.
        String candidate = forwarded.split(",")[0].trim();
        return IP_LITERAL.matcher(candidate).matches() ? candidate : peer;
    }

    private boolean isTrusted(String address) {
        if (address == null) {
            return false;
        }
        return trustedProxies.stream().anyMatch(matcher -> matcher.matches(address));
    }
}
