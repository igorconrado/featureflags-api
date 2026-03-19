CREATE TABLE flags (
    id                  UUID          DEFAULT RANDOM_UUID() PRIMARY KEY,
    "key"               VARCHAR(100)  UNIQUE NOT NULL,
    name                VARCHAR(255)  NOT NULL,
    description         VARCHAR(255),
    enabled             BOOLEAN       DEFAULT false,
    rollout_percentage  INTEGER       DEFAULT 0,
    environments        VARCHAR ARRAY DEFAULT ARRAY[],
    allowed_users       VARCHAR ARRAY DEFAULT ARRAY[],
    tags                VARCHAR ARRAY DEFAULT ARRAY[],
    created_by          UUID          REFERENCES users(id),
    created_at          TIMESTAMP     DEFAULT NOW(),
    updated_at          TIMESTAMP     DEFAULT NOW()
);
