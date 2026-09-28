# ReleasePilot PM Case Study

## Problem

Release decisions are frequently based on incomplete information because requirements, tests, and defects live in separate systems. This creates uncertainty for product, engineering, and QA at the point where accountability matters most.

## Product decision

Start with a narrow release-readiness wedge rather than attempt to replace Jira or test-management suites. The MVP combines evidence already used in a release decision and makes the decision rules explicit.

## Why this scope

The first release focuses on manual/seeded evidence and deterministic rules. This keeps the product explainable, supports demo data, and avoids asking users to grant third-party credentials before value is proven.

## Key trade-off

Jira Cloud synchronization would make onboarding easier but increases security, permissions, and implementation complexity. The next increment is CSV import: it provides customer value without persistent Jira credentials.

## How success will be measured

The team will measure evidence completeness, decision turnaround time, and time-to-resolution for releases initially marked No-Go.

## What is implemented

- Release creation and selection
- Requirement, test-case, test-run, and defect APIs
- Explainable Go/No-Go dashboard
- Fictional ShopSphere checkout demo data
- GitHub Actions build workflow and Codespaces setup

## What is planned

- Jira CSV import
- UI workflows to record test results and defects
- CI test-report ingestion
- AI-assisted test-case drafting with human approval
