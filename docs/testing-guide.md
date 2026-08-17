# Testing guide

Use a disposable database for integration tests.

## Backend

```powershell
cd backend
mvn.cmd test
```

Complete suite with MySQL 8:

```powershell
$env:MYSQL_INTEGRATION_TESTS='true'
$env:DB_URL='jdbc:mysql://localhost:3306/ai_interview_coach_test'
$env:DB_USERNAME='ai_coach'
$env:DB_PASSWORD='your-local-password'
cd backend
mvn.cmd test
```

This covers API/security, lifecycle/report rules, AI parsing/retries, Flyway metadata, persistence, constraints, ownership, and cascades.

## Frontend

```powershell
cd frontend
npm.cmd install
npm.cmd run typecheck
npm.cmd run lint
npm.cmd run test
npm.cmd run build
npm.cmd audit
```

## Real local E2E

Start disposable MySQL, then backend and frontend in separate terminals:

```powershell
$env:SPRING_PROFILES_ACTIVE='test'
$env:SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/ai_interview_coach_e2e'
$env:SPRING_DATASOURCE_USERNAME='ai_coach'
$env:SPRING_DATASOURCE_PASSWORD='your-local-password'
$env:SPRING_FLYWAY_ENABLED='true'
$env:SPRING_JPA_HIBERNATE_DDL_AUTO='validate'
$env:APP_FRONTEND_URL='http://localhost:3000'
cd backend
mvn.cmd spring-boot:run
```

```powershell
$env:NEXT_PUBLIC_API_URL='http://localhost:8080'
cd frontend
npm.cmd run dev
```

Then run:

```powershell
cd frontend
npx.cmd playwright install chromium
npm.cmd run test:e2e
```

The suite creates data; never point it at valued records. It verifies IT/Non-IT interviews, auth restoration/logout/expiry, answers/follow-ups, completion/abandonment, reports, dashboard, history/pagination, profile, ownership, CORS, validation, loading/network/invalid-session states, and mobile overflow.

Deterministic AI makes these real browser/HTTP/security/service/JPA/MySQL tests repeatable; it does not constitute a live Groq call. A Groq smoke test needs a valid key and the development profile. Actual dated results are in `docs/build-status.md`.
