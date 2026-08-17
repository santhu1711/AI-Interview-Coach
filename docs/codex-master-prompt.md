# AI Interview Coach — Complete Codex Build Prompt

You are a senior Java full-stack developer, Spring Boot architect, Next.js developer, AI integration engineer, database engineer, security engineer, QA engineer, UI/UX designer, and technical documentation specialist.

Build a complete, professional, portfolio-ready full-stack application named:

# AI Interview Coach

The application must support interview preparation for both:

1. IT Field
2. Non-IT Field

Build the complete application inside the currently opened VS Code workspace.

---

# 1. CRITICAL AUTONOMOUS WORKING RULES

Follow these instructions throughout the entire project:

1. Do not ask me questions.
2. Do not wait for my approval between phases.
3. Use reasonable professional defaults whenever something is unspecified.
4. Do not stop after explaining a plan.
5. Inspect the current workspace before creating files.
6. Since this is a fresh project, start from Phase 1.
7. Create all required files and folders yourself.
8. Install all required dependencies yourself.
9. Run all necessary commands yourself.
10. Build real working functionality, not mock screens or pseudocode.
11. Do not leave incomplete business logic, unfinished TODOs, or placeholder APIs.
12. Properly connect frontend, backend, database, authentication, and AI.
13. Test every development phase before moving to the next phase.
14. Do not continue when the current phase has failing tests.
15. Fix compilation, dependency, database, runtime, API, security, frontend, and integration errors.
16. Re-run tests after fixing errors.
17. Mock services may be used only for isolated automated tests.
18. Mocks must not be the only proof that the application works.
19. Perform real local frontend, backend, MySQL, authentication, and API verification.
20. Do not access or modify files outside this project workspace.
21. Do not expose passwords, JWT secrets, database credentials, API keys, or tokens.
22. Never hard-code real credentials.
23. Create `.env.example` files.
24. Create a Git repository.
25. Create a Git commit after every successfully completed and tested phase.
26. Do not deploy the application.
27. Continue automatically through all local-development phases.
28. Stop only after the complete local application has been implemented, tested, and documented.
29. Do not falsely report tests as passing.
30. If a test cannot run, state the exact blocker.

If an external credential such as the Groq API key is unavailable:

* Do not ask me for the key.
* Create the correct environment-variable configuration.
* Continue implementing everything else.
* Create a deterministic test-only AI provider.
* Enable that provider only in the test profile.
* Never use the test provider in development or production.
* Report the pending real Groq smoke test at the end.

---

# 2. SAVE THIS SPECIFICATION

Create:

```text
docs/codex-master-prompt.md
```

Save this complete project specification inside that file.

This file must be used as the source of truth if the Codex session is interrupted.

---

# 3. RECOVERY AFTER INTERRUPTION

If the task is interrupted because of:

* Internet disconnection
* VS Code closing
* Laptop shutdown
* Sleep or hibernation
* Codex stopping
* Maven failure
* npm failure
* MySQL failure
* Test failure

then when work resumes:

1. Read `docs/codex-master-prompt.md`.
2. Run `git status`.
3. Run `git log --oneline --all`.
4. Inspect modified and untracked files.
5. Determine the last completed and tested phase.
6. Determine the first incomplete phase.
7. Preserve all correct existing work.
8. Do not restart from Phase 1 unless the project files truly do not exist.
9. Restart required local services.
10. Continue from the first incomplete phase.
11. Test the interrupted phase before continuing.

---

# 4. DO NOT DEPLOY

This project must remain local until the project owner manually verifies it.

Do not configure or perform:

* Vercel deployment
* Railway deployment
* Render deployment
* AWS deployment
* Azure deployment
* Google Cloud deployment
* Production database hosting
* Domain setup
* SSL setup
* Kubernetes
* Docker deployment
* Docker Compose deployment
* Cloud deployment pipelines
* Production CI/CD deployment

The current workflow is:

```text
Build locally
→ Test locally
→ Fix errors
→ Verify every feature
→ Document locally
→ Stop
```

Deployment will be handled later as a separate phase.

---

# 5. TECHNOLOGY STACK

## Frontend

Use:

* Next.js with App Router
* TypeScript
* Tailwind CSS
* shadcn/ui
* React Hook Form
* Zod
* Axios
* Recharts
* Lucide React icons
* Vitest or Jest where appropriate
* Playwright for end-to-end testing

Use stable mutually compatible versions.

Do not intentionally install known vulnerable package versions.

## Backend

Use:

* Java 21
* Spring Boot
* Maven
* Spring Web
* Spring Data JPA
* Spring Security
* JWT authentication
* BCrypt
* Bean Validation
* MySQL
* Flyway
* Spring Boot Actuator
* Spring WebClient
* Jackson
* Swagger/OpenAPI
* JUnit 5
* Mockito
* Spring Boot Test
* MockMvc

Prefer the latest stable Spring Boot release compatible with Java 21 and all required libraries.

## AI Provider

Use the Groq OpenAI-compatible API.

Environment variables:

```env
GROQ_API_KEY=
GROQ_API_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=llama-3.3-70b-versatile
```

The model and API base URL must remain configurable.

Never hard-code the Groq API key.

## Database

Use local MySQL.

Environment variables:

```env
DB_URL=jdbc:mysql://localhost:3306/ai_interview_coach
DB_USERNAME=
DB_PASSWORD=
```

---

# 6. PROJECT STRUCTURE

Use the currently opened VS Code folder as the project root.

Create:

```text
frontend/
backend/
docs/
.gitignore
README.md
LICENSE
```

Do not create another nested `ai-interview-coach` directory if the currently opened folder is already the project root.

Backend structure:

```text
backend/src/main/java/com/aiinterviewcoach/
├── AiInterviewCoachApplication.java
├── config/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── enums/
├── exception/
├── mapper/
├── repository/
├── security/
├── service/
│   ├── auth/
│   ├── interview/
│   ├── ai/
│   ├── report/
│   └── dashboard/
├── validation/
└── util/
```

Frontend structure:

```text
frontend/src/
├── app/
├── components/
│   ├── auth/
│   ├── dashboard/
│   ├── interview/
│   ├── layout/
│   ├── report/
│   └── ui/
├── contexts/
├── hooks/
├── lib/
├── services/
├── types/
└── validations/
```

Use:

```text
Controller
→ Service
→ Repository
→ MySQL
```

Keep controllers thin.

Keep business logic inside services.

Use constructor injection.

Do not use field injection.

Use DTOs for API requests and responses.

Do not expose JPA entities directly.

---

# 7. APPLICATION PURPOSE

The user must be able to:

1. Register.
2. Login.
3. View their dashboard.
4. Select IT Field or Non-IT Field.
5. Select an interview domain.
6. Select an interview mode.
7. Enter a specific topic.
8. Enter a target job role.
9. Select experience level.
10. Select difficulty.
11. Select number of questions.
12. Start an AI-powered interview.
13. Receive one question at a time.
14. Submit answers.
15. Receive adaptive follow-up questions.
16. Complete or manually end an interview.
17. Generate a detailed evaluation report.
18. View interview history.
19. Continue an active interview after browser refresh.
20. Track performance over time.
21. Compare IT and Non-IT performance.
22. Search and filter history.
23. Delete their own interviews.
24. Update their profile.
25. Access only their own data.

---

# 8. FIELD CATEGORIES

Create:

```java
public enum FieldCategory {
    IT,
    NON_IT
}
```

Display:

```text
IT → IT Field
NON_IT → Non-IT Field
```

Store the field category in every interview session.

---

# 9. IT FIELD

Support these domains:

```text
JAVA
SPRING_BOOT
DSA
SQL
PYTHON
JAVASCRIPT
TYPESCRIPT
REACT
NEXT_JS
FRONTEND_DEVELOPMENT
BACKEND_DEVELOPMENT
FULL_STACK_DEVELOPMENT
DEVOPS
CLOUD_COMPUTING
CYBERSECURITY
DATA_SCIENCE
ARTIFICIAL_INTELLIGENCE
MACHINE_LEARNING
IOT
EMBEDDED_SYSTEMS
SYSTEM_DESIGN
SOFTWARE_TESTING
TECHNICAL_SUPPORT
CUSTOM
```

Display proper readable labels.

Examples:

```text
SPRING_BOOT → Spring Boot
NEXT_JS → Next.js
DATA_SCIENCE → Data Science
ARTIFICIAL_INTELLIGENCE → Artificial Intelligence
TECHNICAL_SUPPORT → Technical Support
```

## IT interview modes

```text
TECHNICAL
CODING
CONCEPTUAL
SCENARIO_BASED
DEBUGGING
SYSTEM_DESIGN
MIXED
```

Evaluate:

* Technical accuracy
* Conceptual understanding
* Problem-solving
* Practical application
* Debugging ability
* Technical communication
* System thinking
* Architecture knowledge
* Trade-off understanding
* Edge-case awareness

---

# 10. NON-IT FIELD

Support:

```text
HUMAN_RESOURCES
CUSTOMER_SUPPORT
CUSTOMER_SUCCESS
SALES
MARKETING
DIGITAL_MARKETING
BUSINESS_DEVELOPMENT
OPERATIONS
PROJECT_COORDINATION
ADMINISTRATION
FINANCE
BANKING
TEACHING
TRAINING
CONTENT_WRITING
RECRUITMENT
MANAGEMENT
LEADERSHIP
GENERAL_HR
CUSTOM
```

Display professional labels.

## Non-IT interview modes

```text
HR
BEHAVIOURAL
SITUATIONAL
ROLE_SPECIFIC
COMMUNICATION
CUSTOMER_HANDLING
LEADERSHIP
MIXED
```

Evaluate:

* Communication
* Confidence
* Role understanding
* Professional behaviour
* Customer handling
* Situational judgement
* Problem-solving
* Decision-making
* Team collaboration
* Leadership
* Answer clarity
* STAR-format response structure where appropriate

Do not ask programming questions during Non-IT interviews unless a custom domain explicitly requires technical questions.

---

# 11. DYNAMIC INTERVIEW SETUP

The interview setup page must initially display two large cards:

```text
IT Field

Non-IT Field
```

When IT is selected:

* Show only IT domains.
* Show only IT modes.
* Label the domain field `Technical Domain`.

When Non-IT is selected:

* Show only Non-IT domains.
* Show only Non-IT modes.
* Label the domain field `Professional Domain`.

Include a CUSTOM domain.

When CUSTOM is selected:

* Show a custom-domain input.
* Make it required.
* Validate its length.

Load options from the backend.

Do not duplicate independent option lists in the frontend.

Store unfinished setup choices in session storage.

Clear them after successful interview creation.

---

# 12. DATABASE ENTITIES

Use JPA and Flyway.

## User

Fields:

```text
id
fullName
email
passwordHash
role
createdAt
updatedAt
```

Requirements:

* Unique email
* BCrypt password hashing
* Default USER role
* Never expose passwordHash

Roles:

```text
USER
ADMIN
```

## InterviewSession

Fields:

```text
id
user
fieldCategory
interviewDomain
customDomain
topic
difficulty
interviewMode
targetRole
experienceLevel
totalQuestions
currentQuestionNumber
followUpCount
status
overallScore
startedAt
completedAt
createdAt
updatedAt
version
```

Use UUID for interview IDs.

Statuses:

```text
CREATED
IN_PROGRESS
COMPLETED
ABANDONED
REPORT_GENERATED
```

Difficulty:

```text
EASY
MEDIUM
HARD
```

Experience:

```text
BEGINNER
INTERMEDIATE
EXPERIENCED
```

Use optimistic locking where useful.

## InterviewMessage

Fields:

```text
id
interviewSession
role
content
sequenceNumber
questionNumber
questionCategory
answerEvaluation
createdAt
```

Roles:

```text
SYSTEM
ASSISTANT
USER
```

Evaluations:

```text
NOT_APPLICABLE
STRONG
PARTIAL
INCORRECT
```

## InterviewReport

Fields:

```text
id
interviewSession
overallScore
technicalAccuracyScore
conceptualUnderstandingScore
problemSolvingScore
communicationScore
confidenceScore
situationalJudgementScore
roleUnderstandingScore
strengths
weaknesses
revisionAreas
verdict
recommendation
questionFeedbackJson
generatedAt
```

Scores not relevant to a category may be null.

Recommendation:

```text
PASS
FAIL
```

Create:

* Foreign keys
* Indexes
* Unique constraints
* Proper relationships
* Message ordering
* User ownership queries
* One-report-per-interview constraint

---

# 13. FLYWAY

Create:

```text
V1__create_users_table.sql
V2__create_interview_sessions_table.sql
V3__create_interview_messages_table.sql
V4__create_interview_reports_table.sql
V5__create_indexes_and_constraints.sql
```

Do not modify an already-applied migration later.

Use new migrations for future changes.

Test migrations against real local MySQL.

---

# 14. AUTHENTICATION

Implement:

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

Registration request:

```json
{
  "fullName": "Santhosh S",
  "email": "santhosh@example.com",
  "password": "Secure@123",
  "confirmPassword": "Secure@123"
}
```

Validation:

* Full name required
* Valid email required
* Password minimum 8 characters
* Uppercase required
* Lowercase required
* Number required
* Special character required
* Confirmation must match
* Duplicate email returns 409

Login response:

```json
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "fullName": "Santhosh S",
    "email": "santhosh@example.com"
  }
}
```

Use:

```env
JWT_SECRET=
JWT_EXPIRATION=3600000
APP_FRONTEND_URL=http://localhost:3000
```

Use Spring Security.

Use stateless authentication.

Require authentication for:

* Dashboard
* Interviews
* Reports
* History
* Profile

One user must never access another user's:

* Interview
* Transcript
* Report
* Dashboard data
* History

---

# 15. AI INTERVIEWER

Create a dedicated Groq integration service using Spring WebClient.

The AI prompt must receive:

* Field category
* Interview domain
* Custom domain
* Topic
* Interview mode
* Target role
* Experience level
* Difficulty
* Total questions
* Current question number
* Transcript
* Previously covered categories
* Previous answer evaluation
* Follow-up count

The AI must:

1. Act as an expert professional interviewer.
2. Ask exactly one question at a time.
3. Keep a professional and encouraging tone.
4. Never give hints during an interview.
5. Never reveal correct answers during the interview.
6. Never turn the session into a lesson.
7. Avoid repeating questions.
8. Avoid repeatedly asking the same concept.
9. Briefly acknowledge strong answers.
10. Probe partially correct answers with one focused follow-up.
11. Briefly identify an important gap in incorrect answers.
12. Move to another area when repeated probing is unnecessary.
13. Match the role and experience level.
14. Match the selected difficulty.
15. Match IT or Non-IT context.
16. Return structured JSON.

Difficulty rules:

## Easy

* Definitions
* Basic concepts
* Fundamentals
* Simple scenarios
* Basic responsibilities

## Medium

* Practical application
* Comparisons
* Debugging
* Problem-solving
* Workplace scenarios
* Decision-making

## Hard

* Architecture
* Trade-offs
* Scalability
* Performance
* Complex scenarios
* Leadership decisions
* System thinking
* Edge cases

---

# 16. AI RESPONSE FORMAT

Require:

```json
{
  "message": "What is the difference between HashMap and ConcurrentHashMap?",
  "evaluation": "STRONG",
  "questionCategory": "Java Collections",
  "isFollowUp": false,
  "shouldComplete": false
}
```

For the first question:

```text
evaluation = NOT_APPLICABLE
```

Backend must remain responsible for:

* Current question
* Total question count
* Message sequence
* Interview state
* Ownership
* Follow-up limits
* Completion

Do not depend only on an AI completion phrase.

If AI JSON parsing fails:

1. Remove markdown fences.
2. Extract JSON safely.
3. Validate fields.
4. Retry once using a corrective prompt.
5. Return a professional provider error if parsing still fails.
6. Do not crash.
7. Do not persist malformed output.

Handle:

* Missing API key
* Invalid API key
* Rate limit
* Timeout
* Network error
* Empty response
* Invalid JSON
* Provider unavailable

---

# 17. INTERVIEW REPORT

Generate reports only when:

* Interview exists
* Interview belongs to user
* Interview is completed
* Report does not already exist

Avoid duplicate AI report generation.

Required report:

```json
{
  "overallScore": 78,
  "technicalAccuracyScore": 80,
  "conceptualUnderstandingScore": 76,
  "problemSolvingScore": 82,
  "communicationScore": 72,
  "confidenceScore": 75,
  "situationalJudgementScore": null,
  "roleUnderstandingScore": 74,
  "strengths": [
    "Specific strength based on an actual candidate answer."
  ],
  "weaknesses": [
    "Specific weakness based on an actual candidate answer."
  ],
  "revisionAreas": [
    "Topic one",
    "Topic two",
    "Topic three"
  ],
  "verdict": "Professional overall assessment.",
  "recommendation": "PASS",
  "questionFeedback": [
    {
      "question": "Actual question",
      "answerSummary": "Short answer summary",
      "evaluation": "STRONG",
      "feedback": "Constructive feedback"
    }
  ]
}
```

Score guide:

```text
85–100 = Excellent
70–84 = Good
55–69 = Adequate
Below 55 = Weak
```

Default recommendation:

```text
60+ → PASS
Below 60 → FAIL
```

## IT report

Show:

* Overall score
* Technical accuracy
* Conceptual understanding
* Problem-solving
* Communication
* Confidence

## Non-IT report

Show:

* Overall score
* Communication
* Confidence
* Situational judgement
* Role understanding
* Problem-solving

---

# 18. INTERVIEW APIs

Options:

```text
GET /api/interview-options
```

Return:

* Field categories
* Domains
* Modes
* Difficulties
* Experience levels
* Minimum questions
* Maximum questions
* Default questions

Interview lifecycle:

```text
POST   /api/interviews
GET    /api/interviews
GET    /api/interviews/{sessionId}
POST   /api/interviews/{sessionId}/answers
POST   /api/interviews/{sessionId}/complete
POST   /api/interviews/{sessionId}/abandon
DELETE /api/interviews/{sessionId}
```

IT example:

```json
{
  "fieldCategory": "IT",
  "interviewDomain": "JAVA",
  "customDomain": null,
  "topic": "Core Java",
  "difficulty": "MEDIUM",
  "interviewMode": "TECHNICAL",
  "targetRole": "Java Backend Developer",
  "experienceLevel": "BEGINNER",
  "totalQuestions": 10
}
```

Non-IT example:

```json
{
  "fieldCategory": "NON_IT",
  "interviewDomain": "CUSTOMER_SUPPORT",
  "customDomain": null,
  "topic": "Customer complaint handling",
  "difficulty": "MEDIUM",
  "interviewMode": "SITUATIONAL",
  "targetRole": "Customer Support Specialist",
  "experienceLevel": "INTERMEDIATE",
  "totalQuestions": 10
}
```

Submit answer:

```json
{
  "answer": "Candidate answer"
}
```

Validate:

* Blank answer
* Answer maximum length
* Invalid session
* Wrong owner
* Completed session
* Duplicate submission
* Invalid IT/Non-IT domain combinations
* Invalid mode combinations
* Missing custom domain

---

# 19. REPORT APIs

```text
POST /api/interviews/{sessionId}/report
GET  /api/interviews/{sessionId}/report
```

---

# 20. DASHBOARD APIs

```text
GET /api/dashboard/summary
GET /api/dashboard/performance
```

Return:

* Total interviews
* Completed interviews
* Active interviews
* IT interview count
* Non-IT interview count
* Average score
* Average IT score
* Average Non-IT score
* Highest score
* Pass percentage
* Strongest domain
* Weakest domain
* Recent interviews
* Score trend
* Domain performance
* IT versus Non-IT comparison

---

# 21. PROFILE APIs

```text
GET /api/profile
PUT /api/profile
```

Allow updating the user's full name.

Never expose password details.

---

# 22. HEALTH AND SWAGGER

Create:

```text
GET /api/health
GET /actuator/health
```

Configure Swagger/OpenAPI.

Development Swagger URL:

```text
http://localhost:8080/swagger-ui/index.html
```

Add Bearer JWT support to Swagger.

---

# 23. GLOBAL ERROR HANDLING

Use Bean Validation and `@RestControllerAdvice`.

Return consistent JSON:

```json
{
  "timestamp": "timestamp",
  "status": 400,
  "error": "Validation Failed",
  "message": "The request contains invalid values.",
  "path": "/api/interviews",
  "fieldErrors": {
    "topic": "Topic is required."
  }
}
```

Handle:

* Invalid registration
* Duplicate email
* Invalid login
* Missing JWT
* Invalid JWT
* Expired JWT
* Access denied
* Interview not found
* Invalid session state
* Report not found
* AI error
* Database error
* JSON error
* Unexpected errors

Never expose raw stack traces.

---

# 24. FRONTEND PAGES

Create:

```text
/
├── login
├── register
├── dashboard
├── interview/setup
├── interview/[sessionId]
├── report/[sessionId]
├── history
└── profile
```

Also create:

* 404 page
* Loading states
* Skeletons
* Empty states
* Error states
* Retry actions
* Confirmation dialogs
* Toast notifications
* Protected-route handling

---

# 25. UI DESIGN

Create a modern professional interview platform.

Use:

* Dark slate/zinc background
* White/light text
* Blue primary accent
* Green success
* Yellow warning
* Red failure

Use:

* shadcn/ui cards
* Buttons
* Inputs
* Selects
* Textareas
* Badges
* Progress bars
* Skeletons
* Dialogs

Requirements:

* Responsive
* Mobile friendly
* Keyboard accessible
* Professional spacing
* Clear loading states
* No horizontal overflow
* Subtle animation only
* No excessive effects

---

# 26. LANDING PAGE

Heading:

```text
Practise smarter. Interview with confidence.
```

Subtitle:

```text
Take realistic AI-powered interviews for IT and Non-IT careers, receive detailed feedback, and track your improvement over time.
```

Include:

* AI Interview Coach branding
* Start Practising
* Login
* Dashboard button when logged in
* IT Field section
* Non-IT Field section
* How It Works
* Supported domains
* AI report feature
* Performance analytics
* Footer

---

# 27. LOGIN AND REGISTRATION FRONTEND

Use:

* React Hook Form
* Zod
* Show/hide password
* Loading state
* Disabled submit button
* Inline validation
* Server errors
* Duplicate email feedback
* Invalid login feedback

Create:

* Authentication context
* Typed Axios client
* JWT interceptor
* Token expiration handling
* Logout
* Protected navigation

Never pass JWT tokens through URL parameters.

---

# 28. DASHBOARD FRONTEND

Show:

* Welcome user
* Total interviews
* Completed interviews
* Active interviews
* IT count
* Non-IT count
* Average score
* Highest score
* Pass percentage
* Strongest domain
* Weakest domain
* Recent interviews

Use Recharts for:

* Score trend
* Domain performance
* IT vs Non-IT performance

Handle users with no interview history.

---

# 29. INTERVIEW SETUP FRONTEND

First display:

```text
IT Field
Non-IT Field
```

Then dynamically display:

* Domain
* Custom domain
* Topic
* Interview mode
* Target role
* Experience
* Difficulty
* Number of questions

Load configuration from the backend.

On submit:

1. Validate.
2. POST `/api/interviews`.
3. Receive session ID.
4. Navigate to:

```text
/interview/{sessionId}
```

Do not send transcript through URL parameters.

---

# 30. INTERVIEW CHAT FRONTEND

Header:

* IT/Non-IT badge
* Domain
* Topic
* Difficulty
* Mode
* Question progress
* Status
* Timer
* End Interview

Chat:

* AI messages left
* User messages right
* Role labels
* Timestamps
* Professional bubbles
* Auto-scroll
* AI typing indicator

Answer area:

* Textarea
* Character count
* Submit
* Enter submits
* Shift+Enter creates new line
* Disable while AI is processing
* Prevent duplicate submission
* Preserve answer if request fails

On refresh:

* Reload session from backend
* Reload transcript
* Continue active interview

---

# 31. REPORT FRONTEND

Display:

* Interview Complete
* Field
* Domain
* Role
* Difficulty
* Mode
* Experience
* Duration
* Overall score
* PASS/FAIL
* Category-specific scores
* Strengths
* Improvements
* Revision areas
* Verdict
* Question-by-question feedback

Buttons:

* Start New Interview
* Back to Dashboard

Score colours:

```text
70+ = Green
50–69 = Yellow
Below 50 = Red
```

---

# 32. HISTORY FRONTEND

Support:

* Search by topic
* Search by role
* IT/Non-IT filter
* Domain filter
* Mode filter
* Difficulty filter
* Status filter
* Newest
* Oldest
* Highest score
* Lowest score
* Pagination

Actions:

* Continue active interview
* View report
* Delete with confirmation

---

# 33. PROFILE FRONTEND

Display:

* Full name
* Email
* Account created date
* Total interviews
* Average score
* IT count
* Non-IT count

Allow changing full name.

---

# 34. CONFIGURATION

Create:

```text
application.yml
application-dev.yml
application-test.yml
application-prod.yml
```

Backend environment:

```env
SPRING_PROFILES_ACTIVE=dev
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION=
GROQ_API_KEY=
GROQ_API_BASE_URL=
GROQ_MODEL=
APP_FRONTEND_URL=
```

Create:

```text
backend/.env.example
```

Frontend:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Create:

```text
frontend/.env.example
```

On backend startup print:

```text
AI Interview Coach API started successfully
```

Never print secrets.

---

# 35. LOGGING

Log:

* Startup
* Registration
* Login success/failure
* Interview creation
* Answer submission
* Completion
* Abandonment
* Report generation
* AI errors
* Unexpected errors

Never log:

* Passwords
* Password hashes
* API keys
* Full JWT tokens
* Database passwords

---

# 36. TESTING REQUIREMENTS

Testing is mandatory.

## Backend unit tests

Test:

* Registration
* Login
* Password encryption
* JWT generation
* JWT validation
* Expired JWT
* Interview creation
* Category validation
* Domain validation
* Mode validation
* Custom domain
* State transitions
* Question counting
* Message sequencing
* AI parsing
* Report parsing
* Score validation
* Ownership protection
* Dashboard calculations

## Controller tests

Test:

* Valid registration
* Invalid registration
* Duplicate email
* Valid login
* Invalid login
* Protected route without token
* Invalid token
* IT interview
* Non-IT interview
* Wrong domain category
* Wrong mode category
* Missing custom domain
* Blank answer
* Invalid session
* Another user's session
* Completed interview
* Report generation
* Delete ownership

## MySQL verification

Use real local MySQL for final integration testing.

Verify:

* Flyway migrations
* Tables
* Foreign keys
* Unique constraints
* Inserts
* Reads
* Updates
* Deletes
* Interview persistence
* Transcript persistence
* Report persistence
* User isolation

## AI testing

Mock only isolated provider responses for:

* Valid JSON
* Invalid JSON
* Markdown JSON
* Missing field
* Timeout
* Rate limit
* Provider error

When a real Groq key exists, perform one real smoke test.

Verify:

* API authentication
* One question returned
* IT prompt returns relevant IT question
* Non-IT prompt returns relevant Non-IT question

## Frontend

Run:

```text
npm run lint
npm run test
npm run build
```

Test:

* Registration
* Login
* Protected route
* IT selection
* Non-IT selection
* Setup validation
* Chat submission
* Report rendering
* Dashboard
* History filters

## Playwright

Create local test-only end-to-end tests.

IT flow:

```text
Register
→ Login
→ IT Field
→ Java
→ Start Interview
→ Answer
→ Complete
→ Generate Report
→ History
→ Logout
```

Non-IT flow:

```text
Register
→ Login
→ Non-IT Field
→ Customer Support
→ Start Interview
→ Answer
→ Complete
→ Generate Report
→ Dashboard
```

Also test:

* Invalid login
* Invalid registration
* Unauthorized page
* Blank setup
* Blank answer
* Refresh during interview
* Mobile viewport
* Backend unavailable
* Token expiration

---

# 37. PHASE-BY-PHASE DEVELOPMENT

Complete every phase automatically.

## Phase 1 — Project Scaffolding

Create:

* Git repository
* Backend
* Frontend
* Docs
* README
* LICENSE
* `.gitignore`
* Environment examples
* Health endpoint

Run:

* Java version
* Maven compile
* Backend startup
* Node version
* npm installation
* Frontend lint
* Frontend production build

Fix errors.

Commit:

```text
phase-1-project-scaffolding
```

## Phase 2 — Database

Create:

* Enums
* Entities
* Repositories
* Flyway migrations
* MySQL configuration

Test real MySQL when available.

Commit:

```text
phase-2-database-persistence
```

## Phase 3 — Authentication

Implement:

* Registration
* Login
* JWT
* BCrypt
* Spring Security
* Current user
* Ownership foundation

Test completely.

Commit:

```text
phase-3-authentication-security
```

## Phase 4 — IT and Non-IT Configuration

Implement:

* Categories
* Domains
* Modes
* Options endpoint
* Combination validation

Test IT and Non-IT separately.

Commit:

```text
phase-4-interview-categories
```

## Phase 5 — AI Service

Implement:

* Groq WebClient
* Interview prompts
* Structured responses
* Validation
* Retry
* Timeout
* Test provider

Commit:

```text
phase-5-ai-service
```

## Phase 6 — Interview APIs

Implement:

* Start
* Retrieve
* Answer
* Complete
* Abandon
* Delete
* Transcript persistence
* Progress

Test through actual HTTP requests.

Commit:

```text
phase-6-interview-api
```

## Phase 7 — Reports, Dashboard and History Backend

Implement:

* Reports
* History
* Filters
* Dashboard
* Analytics
* Pagination

Test ownership and calculations.

Commit:

```text
phase-7-reports-dashboard-history
```

## Phase 8 — Frontend Foundation

Implement:

* Theme
* Landing
* Login
* Register
* Auth context
* Axios
* Navigation
* Protected pages

Test and build.

Commit:

```text
phase-8-frontend-foundation
```

## Phase 9 — Interview Frontend

Implement:

* IT/Non-IT setup
* Dynamic form
* Chat
* Timer
* Progress
* Refresh recovery
* Completion

Test both IT and Non-IT flows.

Commit:

```text
phase-9-interview-frontend
```

## Phase 10 — Dashboard, Reports, History, Profile

Implement complete frontend.

Test all pages.

Commit:

```text
phase-10-complete-frontend
```

## Phase 11 — Complete Integration

Run:

* Backend tests
* Security tests
* MySQL verification
* Frontend lint
* TypeScript checking
* Frontend tests
* Frontend production build
* Playwright
* IT end-to-end test
* Non-IT end-to-end test
* Groq smoke test if key exists

Fix every failure.

Commit:

```text
phase-11-complete-local-integration
```

## Phase 12 — Final Local Verification

Do not deploy.

Verify:

* Registration
* Login
* Database
* JWT
* IT interview
* Non-IT interview
* AI interaction
* Reports
* History
* Dashboard
* Profile
* Refresh recovery
* User isolation
* Error handling
* Mobile responsiveness

Commit:

```text
phase-12-final-local-verification
```

---

# 38. LOCAL URLS

Use:

```text
Frontend:
http://localhost:3000

Backend:
http://localhost:8080

Swagger:
http://localhost:8080/swagger-ui/index.html

Health:
http://localhost:8080/actuator/health
```

---

# 39. DOCUMENTATION

Create:

```text
docs/
├── codex-master-prompt.md
├── architecture.md
├── api-flow.md
├── database-design.md
├── security.md
├── testing-guide.md
└── local-setup-guide.md
```

Include Mermaid diagrams for:

* Architecture
* Authentication
* Interview lifecycle
* AI flow
* Database ER diagram

README must include:

* Project overview
* IT and Non-IT features
* Technology stack
* Architecture
* Folder structure
* Prerequisites
* Java setup
* Node setup
* MySQL setup
* Environment variables
* Backend commands
* Frontend commands
* Testing commands
* Swagger URL
* Troubleshooting
* Future enhancements
* Author section
* Screenshot placeholders

Do not invent screenshots.

---

# 40. CODE QUALITY

Follow these requirements:

* Clean naming
* Small focused methods
* Constructor injection
* DTO APIs
* No duplicated business logic
* No unused imports
* No dead code
* No obsolete commented-out code
* No hard-coded secrets
* No password exposure
* No raw exception exposure
* No wildcard production CORS
* No transcript in URL
* No JWT in URL
* No frontend-only security
* No user-provided ownership IDs trusted
* No JPA entity exposure
* No fake success messages
* No mocked-only verification
* No test AI provider outside tests
* No duplicate reports
* No known build errors
* No TypeScript errors
* No Maven test failures

---

# 41. FINAL COMPLETION REQUIREMENTS

Do not declare the project complete until:

1. Java backend compiles.
2. Spring Boot starts.
3. Health endpoint works.
4. MySQL connects.
5. Flyway succeeds.
6. Registration works.
7. Login works.
8. Password encryption works.
9. JWT works.
10. User isolation works.
11. IT interview works.
12. Non-IT interview works.
13. Invalid field combinations are rejected.
14. Questions persist.
15. Answers persist.
16. Refresh restores interviews.
17. Completion works.
18. IT report works.
19. Non-IT report works.
20. Reports persist.
21. Dashboard works.
22. History works.
23. Filters work.
24. Profile works.
25. Frontend lint succeeds.
26. TypeScript validation succeeds.
27. Frontend production build succeeds.
28. Backend unit tests pass.
29. Backend integration tests pass.
30. End-to-end tests pass where supported.
31. Documentation is complete.
32. No deployment has been performed.

---

# 42. FINAL REPORT

At completion, report:

1. What was built
2. Final folder structure
3. Important files created
4. Commands executed
5. Git commits created
6. Backend test results
7. MySQL test results
8. Security test results
9. Frontend test results
10. IT interview test result
11. Non-IT interview test result
12. End-to-end test result
13. Groq smoke-test result
14. Environment variables required
15. Backend run command
16. Frontend run command
17. Frontend URL
18. Backend URL
19. Swagger URL
20. Health URL
21. Remaining blockers
22. Manual verification required
23. Known limitations
24. Confirmation that no deployment was performed

Do not ask me what to do next.

Build, test, debug, fix, and document the complete AI Interview Coach application locally and autonomously.


