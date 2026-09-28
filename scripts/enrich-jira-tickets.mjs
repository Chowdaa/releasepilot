/*
 * Enriches the existing ReleasePilot Jira backlog created by
 * create-jira-tickets.mjs. It updates KAN-1 through KAN-15 only; it never
 * creates a ticket.
 */

const baseUrl = (process.env.JIRA_BASE_URL || '').replace(/\/$/, '');
const email = process.env.JIRA_EMAIL;
const token = process.env.JIRA_API_TOKEN;
const projectKey = process.env.JIRA_PROJECT_KEY || 'KAN';

if (!baseUrl || !email || !token) {
  console.error('Missing JIRA_BASE_URL, JIRA_EMAIL, or JIRA_API_TOKEN. No tickets were changed.');
  process.exit(1);
}

const headers = {
  Authorization: `Basic ${Buffer.from(`${email}:${token}`).toString('base64')}`,
  Accept: 'application/json',
  'Content-Type': 'application/json'
};

const paragraph = (text) => ({ type: 'paragraph', content: [{ type: 'text', text }] });
const heading = (text) => ({ type: 'heading', attrs: { level: 2 }, content: [{ type: 'text', text }] });
const bullets = (items) => ({
  type: 'bulletList',
  content: items.map((item) => ({ type: 'listItem', content: [paragraph(item)] }))
});

function ticketDescription({ outcome, story, criteria, qaNotes, outOfScope }) {
  return {
    type: 'doc',
    version: 1,
    content: [
      heading('Outcome'), paragraph(outcome),
      heading('User story'), paragraph(story),
      heading('Acceptance criteria'), bullets(criteria),
      heading('QA and delivery notes'), bullets(qaNotes),
      heading('Out of scope'), paragraph(outOfScope)
    ]
  };
}

const tickets = [
  ['1', 'ReleasePilot MVP - Release Quality Workspace', {
    outcome: 'Deliver an MVP that turns release evidence into an explainable Go or No-Go decision for small SaaS teams.',
    story: 'As a release owner, I want requirements, test evidence, and defects in one workspace so that I can make a defensible release decision.',
    criteria: ['A seeded ShopSphere checkout release demonstrates the end-to-end workflow.', 'The dashboard displays a Go or No-Go result and the rules behind it.', 'The README explains local and Codespaces execution.', 'The MVP uses fictional data only.'],
    qaNotes: ['Treat this Epic as the portfolio release scope.', 'Demo the seeded No-Go scenario first, then describe how a team would resolve it.'],
    outOfScope: 'Enterprise SSO, multi-tenant authorization, and live third-party integrations.'
  }],
  ['2', 'Create and list releases', {
    outcome: 'Give teams a reliable release record that can own all readiness evidence.',
    story: 'As a QA or release owner, I want to create and view releases so that work is organized around a specific delivery date.',
    criteria: ['Users can create a release with a name and target date.', 'The release list is returned by the API and displayed in the UI selector.', 'Invalid or missing release data receives a clear validation response.'],
    qaNotes: ['Test valid creation, blank names, duplicate handling, and date formatting.', 'Verify a newly created release can be selected without a page refresh.'],
    outOfScope: 'Release approvals, release templates, and calendar synchronization.'
  }],
  ['3', 'Manage release requirements', {
    outcome: 'Capture the scope that must be proven before a release is approved.',
    story: 'As a product or QA owner, I want requirements linked to a release so that readiness has traceable scope coverage.',
    criteria: ['A requirement stores title, description, priority, and release reference.', 'Users can create and list requirements for a selected release.', 'Requirements appear in dashboard coverage calculations.'],
    qaNotes: ['Test required fields and invalid release references.', 'Confirm a requirement with no linked test case is counted as uncovered.'],
    outOfScope: 'Full requirements authoring, versioning, and external document sync.'
  }],
  ['4', 'Link test cases to requirements', {
    outcome: 'Make traceability visible from product scope to test evidence.',
    story: 'As a QA engineer, I want test cases linked to requirements so that I can show what has and has not been validated.',
    criteria: ['A test case includes steps, expected result, priority, and one requirement link.', 'Test cases are retrievable by release.', 'Dashboard coverage changes when a requirement gains a test case.'],
    qaNotes: ['Test one requirement with multiple test cases and a requirement with none.', 'Verify invalid requirement IDs cannot create orphan test cases.'],
    outOfScope: 'Test-case version history, parameterization, and test management vendor migration.'
  }],
  ['5', 'Record test-run results', {
    outcome: 'Store execution evidence used by the release decision.',
    story: 'As a QA engineer, I want to record test results so that the team can distinguish planned testing from completed testing.',
    criteria: ['A test run records a test case, result, execution type, and timestamp.', 'Passed, failed, and skipped outcomes are supported.', 'Dashboard pass-rate and failed-run metrics update from recorded runs.'],
    qaNotes: ['Test all outcomes and repeated runs of the same test case.', 'Verify the denominator for pass rate excludes statuses only if documented.'],
    outOfScope: 'Remote test execution, CI result ingestion, and attachments.'
  }],
  ['6', 'Log release defects', {
    outcome: 'Expose unresolved release risk instead of hiding it behind test metrics.',
    story: 'As a release owner, I want to log defects against a release so that severity and open blockers affect the decision.',
    criteria: ['A defect stores summary, severity, status, release, and optional test-run reference.', 'Open defects are listed by release.', 'Open blocker defects force a No-Go outcome.'],
    qaNotes: ['Test blocker, critical, major, and minor severities.', 'Verify closed blockers no longer count as release blockers.'],
    outOfScope: 'Defect assignment, comments, SLA tracking, and external bug tracker sync.'
  }],
  ['7', 'Calculate Go-No-Go release decision', {
    outcome: 'Provide a consistent, explainable recommendation instead of a subjective release meeting.',
    story: 'As a stakeholder, I want a Go or No-Go recommendation with reasons so that I can act on the highest-risk evidence.',
    criteria: ['No-Go is returned for an open blocker.', 'No-Go is returned for missing test-case coverage or no execution evidence.', 'No-Go is returned when pass rate is below 90 percent.', 'The API returns every rule that contributed to the decision.'],
    qaNotes: ['Create isolated test data for each rule and a clean Go scenario.', 'Unit-test boundary values including exactly 90 percent.'],
    outOfScope: 'Machine-learning risk scores and configurable organization-wide policy rules.'
  }],
  ['8', 'Build React release dashboard', {
    outcome: 'Make release evidence legible to non-technical stakeholders in a browser.',
    story: 'As a product or engineering leader, I want a concise dashboard so that I can understand readiness without querying APIs.',
    criteria: ['Users can select or create a release.', 'Dashboard displays decision, reasons, requirements, test cases, pass rate, defects, blockers, and failed runs.', 'API errors are displayed clearly without breaking the page.'],
    qaNotes: ['Verify layout at desktop and narrow browser widths.', 'Test loading, empty, error, Go, and No-Go states.'],
    outOfScope: 'Authentication, custom dashboards, and mobile-native applications.'
  }],
  ['9', 'Add fictional ShopSphere demo data', {
    outcome: 'Provide a safe, repeatable demo that immediately demonstrates the product value.',
    story: 'As a recruiter or reviewer, I want realistic seeded data so that I can evaluate the product without manual setup.',
    criteria: ['Seed data represents a fictional e-commerce checkout release.', 'The default dashboard shows a No-Go decision with a blocker and a 66.67 percent pass rate.', 'No customer, employer, or production data is included.'],
    qaNotes: ['Reset the local database and verify the seed state is deterministic.', 'Validate the displayed counts against the seeded records.'],
    outOfScope: 'Production-like data volumes and data import from real companies.'
  }],
  ['10', 'Add Docker execution setup', {
    outcome: 'Enable a consistent local environment for reviewers with Docker installed.',
    story: 'As a developer, I want containerized startup so that I can run the stack without manually configuring each service.',
    criteria: ['Dockerfiles exist for backend and frontend.', 'Docker Compose starts both services.', 'README includes the command and expected ports.'],
    qaNotes: ['Build from a clean Docker cache.', 'Verify frontend-to-backend API connectivity in the composed environment.'],
    outOfScope: 'Production hosting, Kubernetes, and managed database deployment.'
  }],
  ['11', 'Add GitHub Actions CI', {
    outcome: 'Give the repository a basic automated quality gate.',
    story: 'As a contributor, I want builds validated on push and pull request so that broken changes are detected early.',
    criteria: ['Backend build runs in GitHub Actions.', 'Frontend build runs in GitHub Actions.', 'Workflow triggers on push and pull request.'],
    qaNotes: ['Intentionally break each build locally to confirm the workflow would fail.', 'Verify a clean main-branch run is visible in the Actions tab.'],
    outOfScope: 'Deployment pipelines, security scanning, and test-result publishing.'
  }],
  ['12', 'Configure GitHub Codespaces', {
    outcome: 'Let reviewers run the project from a browser-based development environment.',
    story: 'As a reviewer, I want a Codespaces configuration so that I can start exploring without local setup.',
    criteria: ['The repository contains a devcontainer configuration.', 'Ports 5173 and 8080 are forwarded.', 'README documents how to start frontend and backend in Codespaces.'],
    qaNotes: ['Create a fresh Codespace and verify Java, Node, and Maven availability.', 'Verify both forwarded ports can be opened.'],
    outOfScope: 'Prebuilds, organization policy configuration, and paid Codespaces optimization.'
  }],
  ['13', 'Import Jira tickets from CSV', {
    outcome: 'Make planned work importable rather than trapped in a static portfolio document.',
    story: 'As a product owner, I want Jira CSV imports mapped to ReleasePilot requirements so that I can establish traceability from planned work to readiness evidence.',
    criteria: ['Users can upload a Jira-compatible CSV.', 'Required columns and mapping errors are explained.', 'Imported work can be reviewed before it is added to a release.'],
    qaNotes: ['Test valid files, missing columns, malformed CSV, and duplicate rows.', 'Ensure imported text is safely rendered.'],
    outOfScope: 'Two-way Jira synchronization and unattended scheduled imports.'
  }],
  ['14', 'Add UI workflows for test runs and defects', {
    outcome: 'Remove the need for QA users to call REST endpoints for essential evidence capture.',
    story: 'As a QA engineer, I want forms for test results and defects so that I can keep release evidence current from the application UI.',
    criteria: ['Users can select a test case and record a test outcome.', 'Users can log a defect with severity and status.', 'The dashboard refreshes after a successful submission.'],
    qaNotes: ['Validate required fields, error states, keyboard flow, and duplicate submissions.', 'Regression-test the API workflow after adding the UI.'],
    outOfScope: 'Bulk execution, file attachments, and rich-text defect comments.'
  }],
  ['15', 'Add AI-assisted test-case drafts', {
    outcome: 'Speed up test design while preserving QA accountability.',
    story: 'As a QA engineer, I want AI-generated test-case drafts from acceptance criteria so that I can start with structured coverage and review it before use.',
    criteria: ['A user can submit an acceptance criterion for draft generation.', 'Drafts include preconditions, steps, expected results, and boundary cases.', 'A human must review and explicitly save a draft before it becomes a test case.', 'Prompts and outputs use fictional or user-provided non-sensitive data only.'],
    qaNotes: ['Test empty, ambiguous, and long acceptance criteria.', 'Verify generated output is never persisted without explicit approval.'],
    outOfScope: 'Autonomous approval, execution, defect closure, or training on customer data.'
  }]
];

async function request(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, { ...options, headers: { ...headers, ...(options.headers || {}) } });
  const body = await response.text();
  if (!response.ok) throw new Error(`${response.status} ${response.statusText}: ${body}`);
  return body ? JSON.parse(body) : null;
}

try {
  for (const [number, expectedSummary, content] of tickets) {
    const key = `${projectKey}-${number}`;
    const issue = await request(`/rest/api/3/issue/${encodeURIComponent(key)}?fields=summary`);
    if (issue.fields.summary !== expectedSummary) {
      throw new Error(`Safety check failed for ${key}. Expected "${expectedSummary}" but found "${issue.fields.summary}".`);
    }
    await request(`/rest/api/3/issue/${encodeURIComponent(key)}`, {
      method: 'PUT',
      body: JSON.stringify({ fields: { description: ticketDescription(content) } })
    });
    console.log(`Updated ${key}`);
  }
  console.log('Complete. Updated descriptions for 15 existing ReleasePilot Jira tickets; no new tickets were created.');
} catch (error) {
  console.error(`Jira ticket enrichment stopped: ${error.message}`);
  console.error('Review the tickets already listed as updated before rerunning.');
  process.exit(1);
}
