package com.portifolio.api.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductionSafetyCheckTest {

    private static final String STRONG_ADMIN = "k9#Vt2!qLw8Zr4mP";
    private static final String STRONG_DB = "x7Qm2Lp9Vt4Zr8Ns";

    @Test
    void acceptsStrongCredentials() {
        assertThat(ProductionSafetyCheck.validate(STRONG_ADMIN, STRONG_DB)).isEmpty();
    }

    @Test
    void rejectsShortAdminPassword() {
        assertThat(ProductionSafetyCheck.validate("curta123", STRONG_DB)).hasSize(1);
        assertThat(ProductionSafetyCheck.validate("curta123", STRONG_DB).get(0)).contains("ADMIN_PASSWORD");
    }

    @Test
    void rejectsExamplePlaceholdersFromEnvFile() {
        assertThat(ProductionSafetyCheck.validate("troque-esta-senha-tambem", STRONG_DB)).hasSize(1);
        assertThat(ProductionSafetyCheck.validate(STRONG_ADMIN, "troque-esta-senha")).hasSize(1);
    }

    @Test
    void rejectsDefaultDatabasePasswordAndBlank() {
        assertThat(ProductionSafetyCheck.validate(STRONG_ADMIN, "portfolio")).hasSize(1);
        assertThat(ProductionSafetyCheck.validate(STRONG_ADMIN, "")).hasSize(1);
        assertThat(ProductionSafetyCheck.validate(STRONG_ADMIN, null)).hasSize(1);
    }

    @Test
    void reportsAllProblemsAtOnceAndBlocksStartup() {
        assertThatThrownBy(() -> new ProductionSafetyCheck(new AdminProperties("admin", "admin123"), "portfolio"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_PASSWORD")
                .hasMessageContaining("DB_PASSWORD");
    }
}
