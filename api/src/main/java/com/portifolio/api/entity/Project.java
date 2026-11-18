package com.portifolio.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project extends BaseEntity{

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "image_url",length = 500)
    private String imageUrl;

    @Column(name = "github_url",length = 500)
    private String githubUrl;

    @Column(name = "deploy_url",length = 500)
    private String deployUrl;

    @Column(nullable = false)
    private boolean featured;

    @Column(name = "long_description", length = 3000)
    private String longDescription;

    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "project_technologies", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "technology_order")
    @Column(name = "technology", nullable = false, length = 60)
    private List<String> technologies = new ArrayList<>();

    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "project_features", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "feature_order")
    @Column(name = "feature", nullable = false, length = 255)
    private List<String> features = new ArrayList<>();

    @ElementCollection
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "project_screenshots", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "screenshot_order")
    @Column(name = "screenshot", nullable = false, length = 500)
    private List<String> screenshots = new ArrayList<>();
}
