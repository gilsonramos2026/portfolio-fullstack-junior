package com.portifolio.api.security;

import com.portifolio.api.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef0123456789abcdef".getBytes());

    private JwtService serviceWith(String secret, long minutes) {
        return new JwtService(new SecurityProperties(secret, minutes, List.of("http://localhost:3000")));
    }

    @Test
    void generatedTokenIsValidAndCarriesUsername() {
        JwtService service = serviceWith(SECRET, 30);

        String token = service.generateToken("admin", 0);

        assertThat(service.extractValidUsername(token)).contains("admin");
        assertThat(service.getExpirationSeconds()).isEqualTo(1800);
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService service = serviceWith(SECRET, 30);

        String token = service.generateToken("admin", 0);
        char lastChar = token.charAt(token.length() - 1);
        char replacement = lastChar == 'a' ? 'b' : 'a';
        String tampered = token.substring(0, token.length() - 1) + replacement;

        assertThat(service.extractValidUsername(tampered)).isEmpty();
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String otherSecret = Base64.getEncoder()
                .encodeToString("ffffffffffffffffffffffffffffffffffffffffffffffff".getBytes());
        String token = serviceWith(otherSecret, 30).generateToken("admin", 0);

        assertThat(serviceWith(SECRET, 30).extractValidUsername(token)).isEmpty();
    }

    @Test
    void tokenCarriesTheTokenVersion() {
        JwtService service = serviceWith(SECRET, 30);

        assertThat(service.parse(service.generateToken("admin", 3)))
                .contains(new JwtService.TokenClaims("admin", 3));
    }

    @Test
    void tokenWithoutVersionClaimCountsAsVersionZero() {
        // Token emitido antes de o claim existir (mesma chave e emissor, sem "ver").
        String legacy = Jwts.builder()
                .issuer("portfolio-api")
                .subject("admin")
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();

        assertThat(serviceWith(SECRET, 30).parse(legacy))
                .contains(new JwtService.TokenClaims("admin", 0));
    }

    @Test
    void garbageIsRejected() {
        assertThat(serviceWith(SECRET, 30).extractValidUsername("not-a-jwt")).isEmpty();
    }
}