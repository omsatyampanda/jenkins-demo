# Jenkins Interview Task – Satyam Panda

This repo contains a complete, dockerized solution for the three parts:

- **Part 1:** Scripted pipeline creates a ZIP, generates an HTML report, emails it via MailHog, and publishes the report.
- **Part 2:** Report generation moved to a Jenkins Shared Library in `shared-lib/`.
- **Part 3 (optional):** Minimal custom Jenkins plugin step (instructions included).

## Quick Start

1. Install Docker & Docker Compose.
2. Clone this repo and run:

```bash
docker compose up -d
