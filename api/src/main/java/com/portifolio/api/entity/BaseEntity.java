package com.portifolio.api.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

/**
 * Superclasse com identificador e carimbos de auditoria compartilhados pelas entidades
 */

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
