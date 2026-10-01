/*
 * Creates the ReleasePilot delivery backlog in Jira Cloud.
 * Required environment variables: JIRA_BASE_URL, JIRA_EMAIL, JIRA_API_TOKEN
 * Optional: JIRA_PROJECT_KEY (defaults to KAN)
 */

const baseUrl = (process.env.JIRA_BASE_URL || '').replace(/\/$/, '');
const email = process.env.JIRA_EMAIL;
const token = process.env.JIRA_API_TOKEN;
const projectKey = process.env.JIRA_PROJECT_KEY || 'KAN';

if (!baseUrl || !email || !token) {
  console.error('Missing JIRA_BASE_URL, JIRA_EMAIL, or JIRA_API_TOKEN. No tickets were created.');
  process.exit(1);
}

const authorization = `Basic ${Buffer.from(`${email}:${token}`).toString('base64')}`;
const headers = { Authorization: authorization, Accept: 'application/json', 'Content-Type': 'application/json' };
const description = (text) => ({ type: 'doc', version: 1, content: [{ type: 'paragraph', content: [{ type: 'text', text }] }] });

async function jira(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, { ...options, headers: { ...headers, ...(options.headers || {}) } });
  const body = await response.text();
  if (!response.ok) throw new Error(`${response.status} ${response.statusText}: ${body}`);
  return body ? JSON.parse(body) : null;
}

async function createIssue({ summary, issueType, detail, priority, parent }) {
  const fields = { project: { key: projectKey }, summary, issuetype: { name: issueType }, description: description(detail), priority: { name: priority }, labels: ['releasepilot', 'portfolio'] };
  if (parent) fields.parent = { key: parent };
  return jira('/rest/api/3/issue', { method: 'POST', body: JSON.stringify({ fields }) });
}

const workItems = [
  ['Create and list releases', 'Users can create releases and view the release list through the REST API and web UI.', 'High'],
  ['Manage release requirements', 'Users can create and list requirements linked to a release.', 'High'],
  ['Link test cases to requirements', 'Users can create test cases linked to requirements and view them by release.', 'High'],
  ['Record test-run results', 'Users can record a test execution result for a test case.', 'High'],
  ['Log release defects', 'Users can record and list defects linked to a release and optional test run.', 'High'],
  ['Calculate Go-No-Go release decision', 'Dashboard evaluates blockers, coverage, test evidence, and pass-rate rules with reasons.', 'Highest'],
  ['Build React release dashboard', 'Stakeholders can view release metrics and explainable decision reasons in a browser.', 'High'],
  ['Add fictional ShopSphere demo data', 'Seed safe e-commerce checkout data that demonstrates a No-Go decision.', 'Medium'],
  ['Add Docker execution setup', 'Provide Dockerfiles and Docker Compose configuration for local execution.', 'Medium'],
  ['Add GitHub Actions CI', 'Build the Spring Boot backend and React frontend on push and pull request.', 'Medium'],
  ['Configure GitHub Codespaces', 'Provide a browser-based development configuration with forwarded ports.', 'Medium'],
  ['Import Jira tickets from CSV', 'Users upload a Jira CSV export and map work items to ReleasePilot requirements.', 'High'],
  ['Add UI workflows for test runs and defects', 'QA users can record test outcomes and defects without calling REST APIs.', 'High'],
  ['Add AI-assisted test-case drafts', 'Generate draft test cases from an acceptance criterion with mandatory human approval.', 'Medium']
];

try {
  const project = await jira(`/rest/api/3/project/${encodeURIComponent(projectKey)}`);
  console.log(`Connected to Jira project: ${project.name} (${project.key})`);
  const epic = await createIssue({ summary: 'ReleasePilot MVP - Release Quality Workspace', issueType: 'Epic', detail: 'Deliver a release-readiness workspace that connects requirements, test evidence, defects, and an explainable Go/No-Go decision.', priority: 'High' });
  console.log(`Created epic: ${epic.key}`);
  for (const [summary, detail, priority] of workItems) {
    const issue = await createIssue({ summary, issueType: 'Story', detail, priority, parent: epic.key });
    console.log(`Created ${issue.key}: ${summary}`);
  }
  console.log('Complete. Move completed work items to Done in Jira after reviewing them.');
} catch (error) {
  console.error(`Jira ticket creation stopped: ${error.message}`);
  console.error('Any tickets printed above were created successfully. Do not blindly rerun the script; check Jira first to avoid duplicates.');
  process.exit(1);
}
