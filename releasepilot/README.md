# ReleasePilot

ReleasePilot is a release-quality workspace for teams delivering an e-commerce platform. It connects requirements, test cases, test-run evidence, defects, and an explainable release-readiness decision in one place.

This portfolio project demonstrates backend engineering, quality automation, and product delivery skills. It uses fictional data only.

## MVP outcome

A QA engineer can create a release, add requirements, link test cases, record test results and defects, then see a clear **Go / No-Go** recommendation. The application seeds a fictional ShopSphere checkout release so the dashboard works immediately.

## Stack

- Backend: Java 21, Spring Boot, Spring Data JPA
- Frontend: React, TypeScript, Vite
- Database: H2 for the local MVP (PostgreSQL-ready schema)
- Test automation: Playwright and REST Assured
- Delivery: Docker Compose and GitHub Actions

## Repository layout

```text
releasepilot/
  backend/       Spring Boot API
  frontend/      React web application
  docs/          Product, architecture, and delivery documents
```

## Build order

1. Product brief and backlog
2. Backend domain model and REST API
3. React release dashboard
4. Test-run and defect workflows
5. Automation evidence and release-risk calculation
6. Jira import and AI-assisted test-case drafting

## Run locally

### Option A: Docker

```bash
docker compose up --build
```

Open `http://localhost:5173`. The API is available at `http://localhost:8080`.

### Option B: Run each service

```bash
cd backend
mvn spring-boot:run
```

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open the address Vite prints, usually `http://localhost:5173`.

## API overview

| Feature | Endpoint |
| --- | --- |
| Releases | `GET, POST /api/releases` |
| Requirements | `GET, POST /api/releases/{releaseId}/requirements` |
| Test cases | `GET /api/releases/{releaseId}/test-cases`, `POST /api/test-cases` |
| Test runs | `POST /api/test-runs` |
| Defects | `GET /api/releases/{releaseId}/defects`, `POST /api/defects` |
| Dashboard | `GET /api/releases/{releaseId}/dashboard` |

## Decision rules

The dashboard reports **No-Go** when there is an open blocker defect, a requirement without a linked test case, no test-run evidence, or a pass rate below 90%. The reasons are returned in the API response so the recommendation remains explainable.

## Product documentation

- [Product strategy](docs/PRODUCT_STRATEGY.md)
- [Jira CSV import PRD](docs/PRD.md)
- [Jira import guide and backlog](docs/JIRA_IMPORT_GUIDE.md)

See [the product brief](docs/PROJECT_BRIEF.md) and [the MVP backlog](docs/MVP_BACKLOG.md).
