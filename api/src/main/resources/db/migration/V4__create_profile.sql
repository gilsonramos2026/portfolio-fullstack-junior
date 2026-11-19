CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    role VARCHAR(120) NOT NULL,
    headline VARCHAR(300) NOT NULL,
    bio VARCHAR(1000) NOT NULL,
    about VARCHAR(3000) NOT NULL,
    location VARCHAR(120),
    email VARCHAR(160) NOT NULL,
    github_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    resume_url VARCHAR(500),
    avatar_url VARCHAR(500),
    open_to_work BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);