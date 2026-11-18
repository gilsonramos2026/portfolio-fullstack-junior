CREATE TABLE education (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    institution VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(2000),
    start_date DATE NOT NULL,
    end_date DATE,
    credential_url VARCHAR(500),
    workload_hours INT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_education_type CHECK (type IN ('DEGREE', 'COURSE')),
    CONSTRAINT chk_education_dates CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_education_workload CHECK (workload_hours IS NULL OR workload_hours > 0)
);

CREATE INDEX idx_education_start_date ON education (start_date DESC);