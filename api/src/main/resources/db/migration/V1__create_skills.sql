CREATE TABLE skills (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(80)  NOT NULL UNIQUE,
    category   VARCHAR(20)  NOT NULL,
    level      SMALLINT     NOT NULL DEFAULT 3 CHECK (level BETWEEN 1 AND 5),
    icon_url   VARCHAR(500),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_skill_category CHECK (category IN ('BACKEND', 'FRONTEND', 'DATABASE', 'DEVOPS'))
);

CREATE INDEX idx_skills (category);