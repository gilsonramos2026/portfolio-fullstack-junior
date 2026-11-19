CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    --Versão dos tokens: ao trocar a senha ela aumenta e todos os JWT emitidos antes dexam valer
    token_version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);