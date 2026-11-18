package com.portifolio.api.entity;

import com.portifolio.api.entity.enums.SkillCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skill extends BaseEntity{

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkillCategory category;

    @Column(nullable = false)
    private short level;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

}
