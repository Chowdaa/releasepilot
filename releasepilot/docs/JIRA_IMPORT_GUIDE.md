# Jira Import Guide

## Import the product backlog into Jira

1. Create a Jira Software project named `ReleasePilot`.
2. Go to **Issues** then **Import issues from CSV**.
3. Upload `docs/jira/ReleasePilot_Jira_Backlog.csv`.
4. Map the CSV columns to Summary, Issue Type, Priority, Description, and Status.
5. Import the issues and create an epic named `ReleasePilot MVP`.

## Import Jira requirements into ReleasePilot

1. In Jira, export the relevant backlog or filter as CSV.
2. In ReleasePilot, select the target release.
3. Choose the CSV file in **Import Jira requirements** and click **Import CSV**.
4. ReleasePilot imports Story, Task, and Requirement rows using Summary, Description, Priority, and Issue Type.

This feature deliberately avoids storing Jira credentials in the MVP.
