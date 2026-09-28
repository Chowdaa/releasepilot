# ReleasePilot: Project Brief

## 1. Problem

Release teams commonly use separate systems for requirements, test cases, automation results, and defects. This makes it hard to know whether a release is actually safe to ship and forces QA, development, and product stakeholders to assemble evidence manually.

## 2. Product vision

ReleasePilot provides a single, evidence-based release-quality view. A user can link requirements to test cases, record test results and defects, identify gaps and risk, and make a documented Go / No-Go decision.

## 3. Demo domain

The application uses a fictional e-commerce platform called **ShopSphere**. Its first sample release covers checkout payment validation and order confirmation. No Oracle data, credentials, customer information, or internal documentation will be used.

## 4. Primary users

| User | Need |
| --- | --- |
| QA Engineer | Organize coverage, test evidence, and defects for a release. |
| Engineering Lead | Understand failed tests and blocking defects before approving deployment. |
| Product / Release Manager | See a concise readiness decision and its supporting evidence. |

## 5. MVP user journey

1. A QA engineer creates a release named `ShopSphere 1.0 - Checkout`.
2. They add or import requirements such as `Customer can pay with a valid card`.
3. They create test cases and link them to a requirement.
4. They record an API or UI test-run result.
5. They log a defect when a test fails.
6. The release dashboard calculates coverage, failure rate, open blocking defects, and a Go / No-Go recommendation.

## 6. MVP scope

### Included

- Releases, requirements, test cases, test runs, and defects
- Requirement-to-test-case traceability
- Dashboard metrics and deterministic release-risk calculation
- A manual test-result entry workflow
- Seeded fictional ShopSphere data
- REST API, PostgreSQL persistence, automated backend tests, and a React UI

### Deferred

- Real Jira OAuth integration and syncing
- Production-grade identity provider integration
- Distributed test execution workers
- AI-generated test cases
- Notifications, permissions administration, and external marketplace plugins

## 7. Success criteria

- A user can complete the MVP journey without editing the database directly.
- Every requirement can show linked test cases and current execution status.
- A release shows a transparent Go / No-Go decision with the reasons behind it.
- The application is deployable locally through Docker Compose.

## 8. Release-risk rules for MVP

The initial rules are intentionally deterministic and visible to the user:

| Condition | Effect |
| --- | --- |
| Any open blocker defect | No-Go |
| Any requirement has no linked test case | No-Go |
| Test pass rate below 90% | No-Go |
| Otherwise | Go |

Later versions may add configurable thresholds and AI-assisted summaries, but the decision will remain explainable.
