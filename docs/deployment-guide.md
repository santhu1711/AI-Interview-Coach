# Production deployment preparation

This project is prepared for a Vercel frontend, Railway Spring Boot backend, Railway MySQL, and Groq. Deployment remains a manual owner action.

## Railway backend

Create a Railway service from the GitHub repository and set its root directory to `backend`. The service-root `Dockerfile` builds the Maven application with Java 21 and starts the packaged JAR. Spring listens on `0.0.0.0` and Railway's injected `PORT`; it continues to default to 8080 locally.

Provision Railway MySQL in the same project. Configure these backend service variables:

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` |
| `DB_URL` | JDBC MySQL URL, built from Railway MySQL reference variables |
| `DB_USERNAME` | Reference the Railway MySQL user |
| `DB_PASSWORD` | Reference the Railway MySQL password |
| `JWT_SECRET` | New production-only random secret of at least 32 bytes |
| `JWT_EXPIRATION` | JWT lifetime in milliseconds, for example `3600000` |
| `GROQ_API_KEY` | Groq secret stored only in Railway Variables |
| `GROQ_MODEL` | `openai/gpt-oss-120b` |
| `GROQ_API_BASE_URL` | Optional; defaults to Groq's OpenAI-compatible API |
| `GROQ_TIMEOUT` | Optional duration, such as `20s` |
| `APP_FRONTEND_URL` | Exact HTTPS production origin of the Vercel frontend, without a trailing slash |

Do not manually define `PORT`; Railway injects it. Railway MySQL exposes host, port, database, user, and password variables. Use Railway reference variables to assemble a JDBC URL with this shape:

```text
jdbc:mysql://<private-host>:<private-port>/<database>
```

Do not commit the resolved values. `application-prod.yml` requires the database variables and JWT secret. Flyway is enabled globally and applies immutable migrations V1-V5 before Hibernate validates the schema.

After Railway generates a public HTTPS domain, verify `/actuator/health`, then use that origin as the frontend API URL.

## Vercel frontend

Import the same GitHub repository as a Vercel project and set Root Directory to `frontend`. Vercel detects Next.js and uses `npm install` plus `npm run build` from that directory.

Configure this Vercel environment variable for Production (and separately for Preview only when a compatible backend/CORS origin exists):

| Variable | Purpose |
|---|---|
| `NEXT_PUBLIC_API_URL` | Public HTTPS origin of the Railway backend, without a trailing slash |

`NEXT_PUBLIC_API_URL` is embedded into the browser bundle at build time and is not secret. Redeploy after changing it. The frontend deliberately fails fast when it is missing instead of silently calling localhost or the Vercel origin.

Once the Vercel production URL is known, set the Railway backend's `APP_FRONTEND_URL` to that exact origin and redeploy/restart the backend. Development continues to use `http://localhost:3000` through the backend default and `frontend/.env.example`.

## Pre-deployment checks

```powershell
cd backend
mvn.cmd test

cd ../frontend
$env:NEXT_PUBLIC_API_URL='http://localhost:8080'
npm.cmd run typecheck
npm.cmd run lint
npm.cmd run test
npm.cmd run build
```

Never add production credentials to `.env` files in Git. The repository ignores `.env`, `.env.*`, `.vercel`, runtime databases, logs, build outputs, PIDs, and test artifacts while explicitly retaining `.env.example` templates.
