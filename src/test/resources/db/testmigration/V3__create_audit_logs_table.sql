CREATE TABLE audit_logs (
    id         UUID         DEFAULT RANDOM_UUID() PRIMARY KEY,
    flag_id    UUID         REFERENCES flags(id) ON DELETE CASCADE,
    flag_key   VARCHAR(100) NOT NULL,
    user_id    UUID         REFERENCES users(id) ON DELETE SET NULL,
    user_email VARCHAR(255),
    action     VARCHAR(50)  NOT NULL,
    changes    VARCHAR,
    created_at TIMESTAMP    DEFAULT NOW()
);
