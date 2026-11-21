CREATE TABLE contact_messages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(160) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    source VARCHAR(10) NOT NULL DEFAULT 'FORM',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_message_source CHECK (source IN ('FORM', 'CHAT'))
);

CREATE INDEX idx_contact_messages_created_at ON contact_messages (created_at DESC);
CREATE INDEX idx_contact_messages_unread ON contact_messages (created_at DESC) WHERE is_read = FALSE;