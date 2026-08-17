# Local setup guide

This is the clean-machine resume procedure.

## Prerequisites

Install Java 21, Maven 3.9+, Node.js 20+ with npm, Git, and MySQL 8+. Verify with `java -version`, `mvn.cmd -version`, `node --version`, `npm.cmd --version`, and `mysql --version`.

## Database

```sql
CREATE DATABASE ai_interview_coach CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'ai_coach'@'localhost' IDENTIFIED BY 'choose-a-local-password';
GRANT ALL PRIVILEGES ON ai_interview_coach.* TO 'ai_coach'@'localhost';
FLUSH PRIVILEGES;
```

Use your own password and never store it in tracked files.

## Backend

`backend/.env.example` is a reference; Spring does not automatically load `.env`.

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'
$env:DB_URL='jdbc:mysql://localhost:3306/ai_interview_coach'
$env:DB_USERNAME='ai_coach'
$env:DB_PASSWORD='your-local-password'
$env:JWT_SECRET='a-random-secret-at-least-32-bytes-long'
$env:JWT_EXPIRATION='3600000'
$env:GROQ_API_KEY='your-groq-api-key'
$env:GROQ_API_BASE_URL='https://api.groq.com/openai/v1'
$env:GROQ_MODEL='llama-3.3-70b-versatile'
$env:GROQ_TIMEOUT='20s'
$env:APP_FRONTEND_URL='http://localhost:3000'
cd backend
mvn.cmd spring-boot:run
```

Flyway creates/validates the schema. Check `http://localhost:8080/actuator/health` for `UP`.

## Frontend

In another terminal:

```powershell
cd frontend
$env:NEXT_PUBLIC_API_URL='http://localhost:8080'
npm.cmd install
npm.cmd run dev
```

Visit `http://localhost:3000`; Swagger is at `http://localhost:8080/swagger-ui/index.html`.

## Environment variables

| Variable | Required | Purpose |
|---|---:|---|
| `SPRING_PROFILES_ACTIVE` | Yes | `dev` for normal use |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Yes | MySQL connection |
| `JWT_SECRET` | Yes | Random HMAC secret, minimum 32 bytes |
| `JWT_EXPIRATION` | No | Lifetime in ms; default 3600000 |
| `GROQ_API_KEY` | Yes for AI | Groq credential |
| `GROQ_API_BASE_URL`, `GROQ_MODEL`, `GROQ_TIMEOUT` | No | Provider overrides |
| `APP_FRONTEND_URL` | Yes | Exact CORS origin |
| `NEXT_PUBLIC_API_URL` | Yes | Browser-visible API URL |

To resume, start MySQL, restore variables, start backend, then frontend. Existing data persists. Keep deterministic AI confined to tests, explicitly set `JWT_SECRET`, never edit applied migrations, and do not run E2E against valued data. Deployment is intentionally absent.
