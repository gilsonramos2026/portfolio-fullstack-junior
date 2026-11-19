package com.portifolio.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Perfil do dono do portifólio.Existe um unico registro editável pelo admin
 */

@Entity
@Table(name = "profiles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity{

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 120)
    private String role;

    @Column(nullable = false, length = 300)
    private String headline;

    @Column(nullable = false, length = 1000)
    private String bio;

    @Column(nullable = false, length = 3000)
    private String about;

    @Column(length = 120)
    private String location;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "open_to_work", nullable = false)
    private Boolean openWork;
}
