package com.portifolio.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    // Versão dos tokens JWT: incrementada ao trocar a senha, o que invalida todos os tokens já emitidos.
    @Column(name = "token_version", nullable = false)
    private long tokenVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AdminUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.tokenVersion = 0L;
    }

    public void changePasswordHash(String newHash) {
        this.passwordHash = newHash;
        this.tokenVersion++;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}