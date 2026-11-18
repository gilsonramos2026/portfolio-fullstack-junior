CREATE TABLE projects (
    id     BIGSERIAL PRIMARY KEY,
    title  VARCHAR(120) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    long_description VARCHAR(3000),
    image_url VARCHAR(500),
    github_url VARCHAR(500),
    deploy_url VARCHAR(500),
    featured BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Listas do projeto: cada uma vira uma filha, apagada junto com o projeto (ON DELETE CASCADE)
CREATE TABLE project_technologies (
    project_id BIGINT NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    technology VARCHAR(60) NOT NULL,
    technology_order INT NOT NULL,
    PRIMARY KEY (project_id, technology_order)
);

CREATE TABLE project_features (
    project_id BIGINT NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    feature VARCHAR(255) NOT NULL,
    feature_order INT NOT NULL,
    PRIMARY KEY (project_id, feature_order)
);

CREATE TABLE project_screenshots (
    project_id BIGINT NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    screenshot VARCHAR(500) NOT NULL,
    screenshot_order INT NOT NULL,
    PRIMARY KEY (project_id, screenshot_order)
);