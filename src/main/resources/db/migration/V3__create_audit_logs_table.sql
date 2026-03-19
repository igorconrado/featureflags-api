CREATE TABLE audit_logs (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    flag_id    UUID         REFERENCES flags(id) ON DELETE CASCADE,
    flag_key   VARCHAR(100) NOT NULL,
    user_id    UUID         REFERENCES users(id) ON DELETE SET NULL,
    user_email VARCHAR(255),
    action     VARCHAR(50)  NOT NULL,
    changes    JSONB,
    created_at TIMESTAMP    DEFAULT NOW()
);
