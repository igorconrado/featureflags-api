CREATE TABLE flags (
    id                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    key                 VARCHAR(100)  UNIQUE NOT NULL,
    name                VARCHAR(255)  NOT NULL,
    description         TEXT,
    enabled             BOOLEAN       DEFAULT false,
    rollout_percentage  INTEGER       DEFAULT 0,
    environments        TEXT[]        DEFAULT '{}',
    allowed_users       TEXT[]        DEFAULT '{}',
    tags                TEXT[]        DEFAULT '{}',
    created_by          UUID          REFERENCES users(id),
    created_at          TIMESTAMP     DEFAULT NOW(),
    updated_at          TIMESTAMP     DEFAULT NOW()
);
