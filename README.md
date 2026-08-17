# AI Interview Coach

AI Interview Coach is a local full-stack application for realistic IT and non-IT interview practice, adaptive AI questioning, detailed reports, and performance analytics.

The project is under active phased construction. See `docs/codex-master-prompt.md` for the complete specification.

## Prerequisites

- Java 21
- Maven 3.9+
- Node.js 20+
- MySQL 8+

## Local setup

Copy each `.env.example` to `.env`, provide local credentials, and create the `ai_interview_coach` MySQL database.

```powershell
cd backend
mvn spring-boot:run
```

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- Health: http://localhost:8080/actuator/health

No deployment is configured or performed.

