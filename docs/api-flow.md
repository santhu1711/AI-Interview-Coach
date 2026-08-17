# API and application flows

## Authentication

```mermaid
sequenceDiagram
    participant B as Browser
    participant A as Auth API
    participant D as MySQL
    B->>A: POST register or login
    A->>D: Validate user / BCrypt hash
    D-->>A: User record
    A-->>B: JWT + safe user DTO
    B->>A: GET /api/auth/me with JWT
    A-->>B: Restored user DTO
```

## Interview lifecycle

```mermaid
stateDiagram-v2
    [*] --> CREATED: Persist setup
    CREATED --> IN_PROGRESS: First question
    IN_PROGRESS --> IN_PROGRESS: Answer and next/follow-up
    IN_PROGRESS --> COMPLETED: Limit or manual complete
    IN_PROGRESS --> ABANDONED: Confirm abandon
    COMPLETED --> REPORT_GENERATED: Generate one report
```

`GET /api/interview-options` supplies valid catalogs and constraints. `POST /api/interviews` validates and returns a UUID. Detail GET restores progress/transcript. Answer POST persists one answer and returns one adaptive question. Complete/abandon enforce lifecycle transitions. Report POST/GET creates or restores the single owned report.

## AI flow

```mermaid
sequenceDiagram
    participant I as Interview service
    participant P as Prompt/AI service
    participant G as Groq
    participant D as MySQL
    I->>P: Setup + transcript + progress
    P->>G: Guarded structured prompt
    G-->>P: JSON
    P->>P: Extract, parse, validate
    alt malformed once
        P->>G: Corrective retry
        G-->>P: Corrected JSON
    end
    P-->>I: Valid response
    I->>D: Atomic persistence
```

Malformed output is not persisted. Transient provider failures receive one bounded retry; API errors are sanitized. Active UI never reveals hints, suggested answers, evaluations, or AI reasoning.

## Endpoints

- Public: health, auth registration/login, Actuator health, Swagger/OpenAPI.
- Authenticated: `/api/auth/me`, interview options and lifecycle/history, reports, dashboard summary/performance, and profile GET/PUT.
- History supports search, category/domain/mode/difficulty/status filters, four sorts, and pagination.

Swagger at `http://localhost:8080/swagger-ui/index.html` is the interactive contract inventory.
