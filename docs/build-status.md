# Build status

Last attempted: 2026-08-17

## Current phase

Phase 6 - Interview APIs and Lifecycle (complete)

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
- Phase 3 committed as `d61f525 phase-3-authentication-security`.
- Phase 4 committed as `eb20a88 phase-4-interview-categories`.
- Phase 5 committed as `1bbae7c phase-5-ai-service`.

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

## Phase 4 completion

- Implemented the complete IT and Non-IT domain and interview-mode catalogs from the master specification.
- Added readable backend-owned labels for field categories, domains, modes, difficulties, and experience levels.
- Implemented authenticated `GET /api/interview-options` with category-scoped domains and modes, field-specific domain labels, question-count defaults and bounds, and custom-domain and target-role length constraints.
- Added reusable validation for category/domain/mode compatibility, conditional custom domains, target roles, and question-count bounds.
- Added structured HTTP 400 handling for invalid interview configurations.
- Verified the IT catalog, Non-IT catalog, invalid cross-category combinations, custom-domain behavior, input bounds, authenticated access, and HTTP 401 responses for anonymous access.
- Phase 4-only test suite: PASS (8 tests, 0 failures, 0 errors, 0 skipped).
- Complete backend `mvn test`: BUILD SUCCESS (27 tests discovered, 25 passed, 0 failures, 0 errors, 2 skipped).
- The two skipped tests are the guarded Phase 2 MySQL integration tests because `MYSQL_INTEGRATION_TESTS=true` and database credentials were not present in this Codex process; their required real-MySQL run is already recorded above as PASS with no skipped MySQL tests.

## Phase 5 completion

- Added a configurable Groq OpenAI-compatible WebClient provider with configurable base URL, model, API key, and 20-second default timeout.
- Added the complete interviewer prompt context, difficulty guidance, IT and Non-IT guardrails, transcript handling, follow-up context, and prompt-injection boundaries around provider data.
- Added a strict five-field structured response contract with markdown-fence removal, safe JSON extraction, field and enum validation, first-question evaluation enforcement, and one corrective retry for malformed output.
- Added one retry for transient rate-limit, timeout, network, and provider-unavailable failures; authentication, empty-response, invalid-envelope, and permanent provider failures return professional API errors without exposing provider details.
- Added a deterministic AI provider enabled only by `app.ai.provider=deterministic-test`, which is configured in the test profile and never selected in development or production.
- Corrected the options metadata to match the persisted custom-domain and target-role capacities of 120 and 150 characters.
- Phase 5-only test suite: PASS (18 tests, 0 failures, 0 errors, 0 skipped).
- Complete backend `mvn test`: BUILD SUCCESS (45 tests discovered, 43 passed, 0 failures, 0 errors, 2 skipped).
- The two skipped tests remain the guarded Phase 2 MySQL integration tests; the required real-MySQL verification is already recorded above as PASS.
- Real Groq smoke test: NOT RUN because `GROQ_API_KEY` is not present in this process. No key was requested, logged, or committed.

## Phase 6 completion

- Implemented authenticated create, list, retrieve, answer, complete, abandon, and delete endpoints under `/api/interviews`.
- Session creation validates category/domain/mode compatibility and input bounds, persists the `CREATED` session, obtains exactly one first AI question, persists it, and transitions the session to `IN_PROGRESS` atomically.
- Answer submission persists ordered USER and ASSISTANT messages, records the AI evaluation on the user answer, advances primary question numbers, and limits each primary question to one focused follow-up while tracking the cumulative follow-up count.
- Added backend-owned automatic completion at the configured primary-question limit and guarded manual completion and abandonment transitions.
- Completed and abandoned sessions reject further answers; invalid and duplicate transitions return structured HTTP 409 responses.
- All session retrieval, mutation, listing, and deletion queries are scoped to the authenticated user. Missing and wrong-owner UUIDs intentionally return the same HTTP 404 response.
- Added lightweight owned interview-history responses with progress, configuration, status, score, and timing fields, while detailed retrieval returns the ordered transcript.
- Added explicit transcript removal before session deletion so database state and long-lived JPA persistence contexts remain consistent.
- Enforced one question per accepted AI message and enhanced the deterministic test provider with strong, partial, incorrect, and follow-up behavior.
- Added structured HTTP 400 handling for malformed JSON, enum values, and UUID path parameters.
- Phase 6-only tests: PASS (12 tests, 0 failures, 0 errors, 0 skipped).
- Verified IT and Non-IT creation, first-question persistence, answer evaluation, sequence ordering, capped follow-ups, automatic and manual completion, abandonment, invalid transitions, question limits, duplicate answers, payload validation, history isolation, deletion, and anonymous security through actual HTTP requests.
- Complete backend `mvn test`: BUILD SUCCESS (57 tests discovered, 55 passed, 0 failures, 0 errors, 2 skipped).
- The two skipped tests remain the guarded Phase 2 MySQL integration tests; their required real-MySQL execution is already recorded above as PASS with no skipped MySQL tests.

No deployment was performed.
