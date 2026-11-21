package com.portifolio.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Impede a API de iniciar com credenciais fracas ou de exemplo quando o perfil {@code dev} NÃO está ativo
 * (jar executado diretamente, Docker, servidor). A validação roda na criação do contexto, antes de o admin
 * ser criado ou de qualquer requisição ser atendida.
 */
@Component
@Profile("!dev")
public class ProductionSafetyCheck {

    static final int MIN_ADMIN_PASSWORD_LENGTH = 12;
    private static final Set<String> WEAK_PASSWORDS =
            Set.of("admin123", "admin", "password", "senha123", "changeme", "portfolio", "postgres", "12345678");
    private static final String PLACEHOLDER_PREFIX = "troque";

    public ProductionSafetyCheck(AdminProperties admin,
                                 @Value("${spring.datasource.password:}") String databasePassword) {
        List<String> problems = validate(admin.password(), databasePassword);
        if (!problems.isEmpty()) {
            throw new IllegalStateException(
                    "Configuração insegura; a API não será iniciada:\n - " + String.join("\n - ", problems)
                            + "\nDefina valores próprios nas variáveis de ambiente (veja .env.example). "
                            + "Para desenvolvimento local, use o perfil 'dev' (mvn spring-boot:run já o ativa).");
        }
    }

    static List<String> validate(String adminPassword, String databasePassword) {
        List<String> problems = new ArrayList<>();
        if (adminPassword == null || adminPassword.length() < MIN_ADMIN_PASSWORD_LENGTH) {
            problems.add("ADMIN_PASSWORD deve ter pelo menos " + MIN_ADMIN_PASSWORD_LENGTH + " caracteres");
        } else if (isWeak(adminPassword)) {
            problems.add("ADMIN_PASSWORD é uma senha de exemplo ou muito comum; escolha outra");
        }
        if (databasePassword == null || databasePassword.isBlank() || isWeak(databasePassword)) {
            problems.add("DB_PASSWORD (POSTGRES_PASSWORD) está vazia, é de exemplo ou muito comum; escolha outra");
        }
        return problems;
    }

    private static boolean isWeak(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return WEAK_PASSWORDS.contains(normalized) || normalized.startsWith(PLACEHOLDER_PREFIX);
    }
}

