# ReleasePilot

ReleasePilot is an evidence-based release-quality workspace for teams delivering an e-commerce platform. It connects requirements, test cases, execution evidence, defects, and an explainable release recommendation in one place.

Built as a portfolio project to demonstrate QA engineering, backend development, and product delivery skills. All product data is fictional.

## Live demo

- **Web app:** https://releasepilot-1.onrender.com
- **Backend API:** https://releasepilot.onrender.com/api/releases
- **Source code:** https://github.com/Chowdaa/releasepilot
- **Jira delivery board:** https://reddyreddy44477.atlassian.net/jira/software/projects/KAN/list

> The hosted backend uses an in-memory demo database. Data created in the app may reset when the free Render service restarts.

## What it does

A user can:

1. Create a release.
2. Import Jira Story or Task rows as release requirements.
3. Generate AI-assisted test-case drafts from a requirement.
4. Explicitly review and approve drafts before they become test cases.
5. Record test execution evidence and defects from the UI.
6. See a transparent **GO / NO-GO** release decision with specific reasons.

The application includes fictional ShopSphere checkout data so the dashboard works immediately after startup.

## Portfolio highlights

- **Product thinking:** PRD, strategy, MVP backlog, Jira ticket breakdown, and release-decision rules.
- **QA workflow:** requirement coverage, test-case approval, result capture, defect severity, and release risk.
- **Backend engineering:** Java, Spring Boot, REST APIs, JPA domain model, and workflow tests.
- **Frontend engineering:** React, TypeScript, Vite, and a responsive release dashboard.
- **Delivery:** Docker, GitHub Actions, GitHub Codespaces, and a public Render deployment.
- **Responsible AI:** AI-generated test-case drafts require human approval before storage.

## Architecture

```text
React + TypeScript frontend
        ↓
Spring Boot REST API
        ↓
H2 demo database
```

The React application is deployed as a Render Static Site. The Spring Boot API is deployed as a Render Web Service.

## Stack

- Backend: Java 21, Spring Boot, Spring Data JPA
- Frontend: React, TypeScript, Vite
- Database: H2 for the MVP
- Testing: Spring Boot workflow tests
- Delivery: Docker Compose, GitHub Actions, GitHub Codespaces, Render
- Optional AI: OpenAI Responses API with a local fallback

## Repository layout

```text
releasepilot/
├── backend/       Spring Boot API and tests
├── frontend/      React web application
├── docs/          Product, PRD, strategy, and Jira documentation
├── scripts/       Jira ticket creation and enrichment scripts
└── docker-compose.yml
```

## Decision rules

ReleasePilot reports **NO-GO** when any of these conditions are true:

- An open blocker defect exists.
- A requirement has no linked test case.
- No test-run evidence exists.
- Test pass rate is below 90%.

The dashboard displays the reason for the decision, making release readiness explainable rather than subjective.

## Run locally

### Option A: Docker

```bash
docker compose up --build
```

Open `http://localhost:5173`.  
The API is available at `http://localhost:8080`.

### Option B: Run each service

Start the backend:

```bash
cd backend
mvn spring-boot:run
```

In a second terminal, start the frontend:

```bash
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal, usually `http://localhost:5173`.

## API overview

| Feature | Endpoint |
| --- | --- |
| Releases | `GET, POST /api/releases` |
| Requirements | `GET /api/releases/{releaseId}/requirements` |
| Jira requirement import | `POST /api/releases/{releaseId}/requirements/import` |
| Test cases | `GET /api/releases/{releaseId}/test-cases`, `POST /api/test-cases` |
| Test runs | `GET /api/releases/{releaseId}/test-runs`, `POST /api/test-runs` |
| Defects | `GET /api/releases/{releaseId}/defects`, `POST /api/defects` |
| AI test-case drafts | `POST /api/releases/{releaseId}/test-case-drafts` |
| Release dashboard | `GET /api/releases/{releaseId}/dashboard` |

## Optional AI configuration

The project works without an API key by generating safe local fallback drafts for happy-path, validation, and recovery scenarios.

To use OpenAI for draft generation, set environment variables only in your terminal or deployment environment:

```bash
export OPENAI_API_KEY="your-key"
export OPENAI_MODEL="your-supported-model"
```

Never commit API keys. Regardless of the draft source, a user must click **Approve & create test case** before a generated test case is stored.

## Product documentation

- [Product strategy](docs/PRODUCT_STRATEGY.md)
- [Product requirements document](docs/PRD.md)
- [PM case study](docs/PM_CASE_STUDY.md)
- [Jira import guide](docs/JIRA_IMPORT_GUIDE.md)
- [Project brief](docs/PROJECT_BRIEF.md)
- [MVP backlog](docs/MVP_BACKLOG.md)

## Quality checks

```bash
cd backend && mvn test
cd ../frontend && npm run build
```

Backend workflow tests cover:

- GO decision with passing evidence
- NO-GO decision for missing test coverage
- NO-GO decision for an open blocker defect