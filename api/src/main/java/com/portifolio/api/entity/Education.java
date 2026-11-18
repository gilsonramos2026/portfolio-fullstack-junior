package com.portifolio.api.entity;

import com.portifolio.api.entity.enums.EducationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "education")
public class Education extends BaseEntity{

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 120)
    private String institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EducationType type;

    @Column(length = 2000)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "credential_url", length = 500)
    private String credentialUrl;

    @Column(name = "workload_hours")
    private Integer workloadHours;
}
