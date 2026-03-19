CREATE TABLE users (
    id         UUID         DEFAULT RANDOM_UUID() PRIMARY KEY,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  DEFAULT 'MEMBER',
    created_at TIMESTAMP    DEFAULT NOW(),
    updated_at TIMESTAMP    DEFAULT NOW()
);
