package com.portifolio.api.security;

import com.portifolio.api.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Geração e validação de tokens JWT (HS256).
 */
@Service
public class JwtService {

    private static final String ISSUER = "portfolio-api";
    private static final String VERSION_CLAIM = "ver";

    /** Conteúdo útil de um token válido. */
    public record TokenClaims(String username, long version) {
    }

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtService(SecurityProperties properties) {
        String secret = properties.jwtSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("A propriedade jwtSecret não pode ser vazia");
        }
        if (properties.jwtExpirationMinutes() <= 0) {
            throw new IllegalArgumentException("A propriedade jwtExpirationMinutes deve ser maior que zero");
        }

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = Duration.ofMinutes(properties.jwtExpirationMinutes());
    }

    public String generateToken(String username, long tokenVersion) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username)
                .claim(VERSION_CLAIM, tokenVersion)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Retorna o username contido no token, ou vazio se o token for inválido/expirado.
     */
    public Optional<String> extractValidUsername(String token) {
        return parse(token).map(TokenClaims::username);
    }

    /**
     * Valida assinatura, emissor e expiração. Tokens sem o claim de versão (emitidos antes dele existir)
     * valem como versão 0.
     * Retorna os dados do token, ou vazio se for inválido/expirado.
     */
    public Optional<TokenClaims> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getSubject() == null) {
                return Optional.empty();
            }

            Number version = claims.get(VERSION_CLAIM, Number.class);
            return Optional.of(new TokenClaims(
                    claims.getSubject(),
                    version == null ? 0L : version.longValue()
            ));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
    }
}