# ReleasePilot MVP Backlog

## Milestone 1: Working backend foundation

| ID | User story | Acceptance criteria |
| --- | --- | --- |
| RP-1 | As a user, I can create and view releases. | `POST /api/releases` creates a release; `GET /api/releases` lists releases. |
| RP-2 | As a user, I can add requirements to a release. | A requirement has title, description, priority, and release ID. |
| RP-3 | As a user, I can create test cases linked to requirements. | A test case stores steps, expected result, priority, and one requirement link. |
| RP-4 | As a user, I can record a test run. | A test run stores result, execution type, timestamp, and test-case ID. |
| RP-5 | As a user, I can record a defect for a failed test. | A defect stores summary, severity, status, test-run ID, and release ID. |

## Milestone 2: Release dashboard

| ID | User story | Acceptance criteria |
| --- | --- | --- |
| RP-6 | As a stakeholder, I can see release metrics. | Dashboard shows requirement coverage, test pass rate, and open defects. |
| RP-7 | As a stakeholder, I can see a Go / No-Go decision. | API returns decision and every rule that caused it. |
| RP-8 | As a user, I can inspect linked evidence. | Release details show requirements, test cases, runs, and defects. |

## Milestone 3: Portfolio-grade workflow

| ID | User story | Acceptance criteria |
| --- | --- | --- |
| RP-9 | As a QA engineer, I can run a sample API suite. | A sample suite creates test-run evidence through the API. |
| RP-10 | As a QA engineer, I can run a sample UI suite. | Playwright tests a ShopSphere checkout flow and reports results. |
| RP-11 | As a recruiter, I can run the project locally. | README includes Docker Compose, seed data, and demo instructions. |

## Later enhancement backlog

- CSV import compatible with Jira work-item exports
- Jira Cloud API integration using a user-provided token stored outside source control
- AI-assisted test-case drafting with mandatory human approval
- CI workflow that uploads test reports
- Role-based access control and audit log
