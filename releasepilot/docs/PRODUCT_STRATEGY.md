# ReleasePilot Product Strategy

## Product hypothesis

Small and mid-sized SaaS teams often decide whether to ship using fragmented Jira tickets, test reports, dashboards, and chat threads. ReleasePilot gives QA, engineering, and product a shared, explainable release decision.

This is a hypothesis to validate with prospective users; it is not a claim of existing customer adoption.

## Target customer and users

**Target customer:** B2B SaaS companies with 20-200 employees, regular releases, Jira/GitHub usage, and no dedicated release-management tool.

| User | Job to be done | Current pain |
| --- | --- | --- |
| QA Engineer | Show test coverage and release evidence quickly. | Test results and bugs are spread across tools. |
| Engineering Lead | Decide whether a build is safe to deploy. | Risk is manually assembled shortly before release. |
| Product Manager | Understand delivery risk and communicate a decision. | Status updates do not show traceable evidence. |

## Core value proposition

**Know whether to ship, why, and what must change before the decision can move to Go.**

## MVP

ReleasePilot stores releases, requirements, test cases, test runs, and defects. It applies visible rules:

- No-Go when an open blocker exists
- No-Go when a requirement has no linked test case
- No-Go when no test-run evidence exists
- No-Go when pass rate is below 90%

The current ShopSphere checkout data is fictional and intentionally produces No-Go, which makes the risk explanation demonstrable.

## Roadmap

| Now | Next | Later |
| --- | --- | --- |
| Manual release evidence and explainable decision | Jira CSV import and UI workflows for tests/defects | Jira Cloud sync, CI test-report ingestion, AI-assisted test-case drafts |

## Success metrics

- Activation: percentage of new workspaces with one release and one requirement created
- Evidence completeness: percentage of requirements linked to at least one test case
- Decision turnaround: time from release-ready to documented Go/No-Go
- Risk resolution: time between a No-Go decision and all blocking conditions cleared

## Validation plan

Interview five QA engineers, engineering leads, or product managers who participate in releases. Ask them to describe their last release decision, the tools used, the evidence they trusted, and where delays occurred. Do not pitch ReleasePilot until after the workflow questions.

## Explicit non-goals

- Replace Jira as the system of record
- Replace a full test-management platform in version one
- Generate release approval without human accountability
