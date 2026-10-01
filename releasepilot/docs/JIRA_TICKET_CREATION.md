# Create the ReleasePilot Jira Backlog

This project includes a one-time script that creates the ReleasePilot epic and backlog in Jira Cloud. It reads credentials only from the terminal environment; it never writes them to source code.

## Prerequisites

- Jira site: `https://reddyreddy44477.atlassian.net`
- Jira project key: `KAN`
- Your Atlassian account email address
- A personal Jira API token

## Run from GitHub Codespaces

Open a Codespaces terminal at the repository root and run:

```bash
export JIRA_BASE_URL="https://reddyreddy44477.atlassian.net"
export JIRA_PROJECT_KEY="KAN"
read -p "Atlassian email: " JIRA_EMAIL
export JIRA_EMAIL
read -s -p "Jira API token: " JIRA_API_TOKEN; echo
export JIRA_API_TOKEN
node scripts/create-jira-tickets.mjs
```

## Enrich the created backlog

After the tickets exist, run the following command from the repository root to
add product-quality descriptions, user stories, acceptance criteria, and QA
notes to the existing `KAN-1` through `KAN-15` tickets. This command updates
only those existing tickets and does not create new ones.

```bash
node scripts/enrich-jira-tickets.mjs
```

The token is requested without echoing it and is available only in the current terminal session. Close the terminal or run `unset JIRA_API_TOKEN` after the script completes.

## Expected result

The script creates one epic and fourteen stories. Jira uses the project's default workflow, so review the created tickets and move completed MVP work to Done manually.

## Safety

- Never paste the API token into GitHub, chat, or a source file.
- Do not run the script twice; it intentionally stops on an error to reduce duplicate-ticket risk.
