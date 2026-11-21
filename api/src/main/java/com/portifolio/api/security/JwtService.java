package com.portifolio.api.security;

import com.portifolio.api.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final String ISSUER = "portfolio-api";
    private static final String VERSION_CLAIM = "ver";

    public record TokenClaims(String username, long version) {
    }

    private final SecretKey signingKey;
    private final Duration expiration;

    public JwtService(SecurityProperties properties) {
        this.signingKey = buildSigningKey(properties.jwtSecret());
        this.expiration = Duration.ofMinutes(properties.jwtExpirationMinutes());
    }

    private SecretKey buildSigningKey(String rawSecret) {
        if (rawSecret == null || rawSecret.isBlank()) {
            throw new IllegalArgumentException("jwtSecret não pode estar vazio");
        }
        String trimmed = rawSecret.trim();
        byte[] keyBytes;
        try {
            if (trimmed.contains("-") || trimmed.contains("_")) {
                keyBytes = Decoders.BASE64URL.decode(trimmed);
            } else {
                keyBytes = Decoders.BASE64.decode(trimmed);
            }
        } catch (DecodingException | IllegalArgumentException ex) {
            keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
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

    public Optional<String> extractValidUsername(String token) {
        return parse(token).map(TokenClaims::username);
    }

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
            return Optional.of(new TokenClaims(claims.getSubject(), version == null ? 0L : version.longValue()));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expiration.toSeconds();
    }
}