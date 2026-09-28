# PRD: Jira CSV Import

## Problem

Teams may want to evaluate Jira requirements in ReleasePilot but hesitate to provide Jira credentials during early adoption.

## Goal

Allow a release manager to import Jira-exported Story and Task rows from CSV into an existing release in under one minute.

## User story

As a release manager, I want to upload a Jira CSV export so that ReleasePilot can start tracking requirement coverage without a Jira integration setup.

## Functional requirements

- The UI accepts `.csv` files for the selected release.
- The backend reads `Summary`, `Description`, `Priority`, and `Issue Type` columns.
- Story, Task, and Requirement rows become requirements.
- Unsupported issue types and rows without Summary are skipped and reported.
- Priority values map to ReleasePilot priorities; unknown values default to Medium.
- The dashboard refreshes after a successful import.

## Non-goals

- Live Jira sync, OAuth, webhooks, custom fields, and duplicate detection.
- Automatic creation of test cases from imported requirements.

## Success metrics

- Import completion rate
- Median time from upload to first dashboard view
- Percentage of imported requirements subsequently linked to a test case
- Number of skipped rows by reason

## Follow-up roadmap

1. CSV preview and duplicate detection
2. Jira Cloud OAuth import
3. GitHub Actions test-result ingestion
4. AI-assisted test-case drafts with required human approval
