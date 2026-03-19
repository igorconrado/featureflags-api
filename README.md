# featureflags-api

Feature flag platform with gradual rollout and audit log built with Java and Spring Boot.

## Features
- Create and manage feature flags with unique keys
- Enable/disable flags globally
- Gradual rollout by percentage (consistent hashing per userId)
- Environment-based activation (production, staging, etc.)
- Allowlist specific users regardless of rollout
- Full audit log with before/after changes
- Public evaluation API (no auth required — for SDK integration)
- Bulk flag evaluation in a single request
- Role-based access: OWNER, ADMIN, MEMBER

## Tech Stack
- Java 21
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway (migrations)
- Docker + Docker Compose
- JUnit 5 + Mockito

## Getting Started

### Running with Docker
```bash
git clone https://github.com/igorconrado/featureflags-api
cd featureflags-api
cp .env.example .env
docker-compose up
```

### Running locally
```bash
cp .env.example .env
./mvnw spring-boot:run
```

## Evaluation Logic

For a given `flagKey + userId + environment`, the engine evaluates in order:

1. Flag disabled globally → `false`
2. userId in allowedUsers → `true`
3. environment not in flag's environments → `false`
4. rolloutPercentage == 100 → `true`
5. rolloutPercentage == 0 → `false`
6. hash(flagKey + userId) % 100 < rolloutPercentage → `true/false`

Same userId always gets the same result (consistent hashing).

## API Endpoints

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Register (first user = OWNER) |
| POST | /api/auth/login | Login |

### Flags
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | /api/flags | Required | List all flags |
| GET | /api/flags/{key} | Required | Get flag by key |
| POST | /api/flags | Required | Create flag |
| PUT | /api/flags/{key} | Required | Update flag |
| DELETE | /api/flags/{key} | Required | Delete flag |
| PATCH | /api/flags/{key}/enable | Required | Enable flag |
| PATCH | /api/flags/{key}/disable | Required | Disable flag |
| PATCH | /api/flags/{key}/rollout | Required | Set rollout % |

### Evaluation (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | /api/evaluate | Evaluate single flag |
| POST | /api/evaluate/bulk | Evaluate multiple flags |
| GET | /api/evaluate/{key} | Quick evaluation via query params |

### Audit
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | /api/audit | Required | All audit logs |
| GET | /api/audit/{flagKey} | Required | Logs for specific flag |

## Environment Variables
```
DB_URL=jdbc:postgresql://localhost:5432/featureflags
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-minimum-32-chars
JWT_EXPIRATION=86400000
```

## Running Tests
```bash
./mvnw test
```
