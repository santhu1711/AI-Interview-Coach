# Build status

Last attempted: 2026-08-17

## Current phase

Phase 3 - Authentication and Security (complete)

## Completed

- Saved the complete source specification in `docs/codex-master-prompt.md`.
- Created the backend Maven project, Spring Boot entry point, profile configuration, environment example, health endpoint, and health controller test.
- Created the frontend Next.js project, environment example, responsive landing page, global theme, and not-found page.
- Added root repository documentation, license, and ignore rules.
- Added an explicit stateless Spring Security filter chain.
- Made `/api/health`, `/actuator/health`, `/swagger-ui/**`, and `/v3/api-docs/**` publicly accessible.
- Kept all other endpoints authenticated with an HTTP 401 response for unauthenticated requests.

## Verification results

- Maven dependency resolution: resolved in the user's normal VS Code terminal.
- Backend `mvn test`: PASS (5 tests, 0 failures, 0 errors, 0 skipped).
- `GET /api/health` without authentication: PASS (HTTP 200).
- `GET /actuator/health` without authentication: PASS (HTTP 200 through the full application security chain).
- Unlisted API route without authentication: PASS (HTTP 401), confirming the public allowlist is narrow.
- `GET /v3/api-docs` without authentication: PASS (HTTP 200).
- `GET /swagger-ui/index.html` without authentication: PASS (HTTP 200).
- Frontend `npm.cmd run lint`: PASS (0 errors, 0 warnings).
- Frontend `npm.cmd run build`: PASS with Next.js 16.3.1; TypeScript validation and static generation completed successfully.
- Frontend `npm.cmd audit`: PASS (0 vulnerabilities).
- Upgraded the direct `next` and `eslint-config-next` dependencies to 16.3.1 because the supported 15.x patch still contained vulnerable bundled PostCSS and Sharp versions.

## Deferred external verification

- No Groq smoke test can run until `GROQ_API_KEY` is supplied in the local environment.

## Phase checkpoints

- Phase 1 committed as `d69a28d phase-1-project-scaffolding`.
- Phase 2 committed as `62799d4 phase-2-database-persistence`.

## Phase 2 progress

- Added all required user, interview session, interview message, and interview report JPA entities.
- Added field, domain, mode, state, evaluation, recommendation, and role enums.
- Added ownership-aware Spring Data repositories and ordered transcript queries.
- Added Flyway migrations `V1` through `V5` with foreign keys, checks, indexes, unique message ordering, cascading cleanup, and one-report-per-interview enforcement.
- User-confirmed real MySQL startup: PASS against MySQL 8.0.46. Flyway applied V1-V5 successfully, Hibernate/JPA initialized, and Spring Boot started on port 8080.
- Added guarded real-MySQL integration coverage for Flyway history, schema metadata, foreign keys, indexes, unique constraints, nullability, enum round-trips, ownership queries, graph CRUD, and cascade deletion.
- Real MySQL integration suite: PASS against MySQL 8.0.46 with no skipped MySQL tests.
- Flyway V1-V5 history and resulting tables: PASS.
- Foreign keys, indexes, unique constraints, and nullable constraints: PASS.
- Enum persistence for categories, domains, modes, difficulty, experience, status, message roles, evaluations, and recommendations: PASS.
- Real CRUD for `User`, `InterviewSession`, `InterviewMessage`, and `InterviewReport`: PASS.
- Ownership queries, ordered transcripts, and cascade deletion: PASS.
- Complete backend `mvn test`: BUILD SUCCESS (10 tests passed, 0 failures, 0 errors, 0 skipped).
- H2 repository verification: PASS for UUID persistence, ownership isolation, transcript ordering, sequence calculation, and duplicate-report rejection.

## Phase 2 completion

- All required Phase 2 database and persistence checks passed against real local MySQL.
- No credentials or secrets are stored in the repository.
- Phase 2 was committed as `phase-2-database-persistence`.

## Phase 3 completion

- Implemented `POST /api/auth/register`, `POST /api/auth/login`, and authenticated `GET /api/auth/me`.
- Added BCrypt password hashing, normalized case-insensitive emails, confirmation checks, duplicate-race handling, and password complexity validation.
- Added signed JWT generation, signature and expiration validation, stateless bearer authentication, and a database-backed principal carrying the trusted user ID.
- Kept health, Swagger, registration, and login public while all other routes remain authenticated by default.
- Added restricted frontend-origin CORS and Swagger Bearer JWT support.
- Added consistent validation, conflict, unauthorized, forbidden, not-found, and unexpected error responses.
- Redacted passwords, confirmations, and access tokens from DTO string representations used by framework debug logging.
- Phase 3-only test suite: PASS (9 tests, 0 failures, 0 errors, 0 skipped).
- Verified registration validation, BCrypt persistence, duplicate email handling, confirmation mismatch, valid and invalid login, protected current-user access, missing and invalid JWT handling, token signature validation, token tampering, token expiry, and weak-secret rejection.

No deployment was performed.
