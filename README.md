# Task: Jenkins build + shared library (Satyam Panda)

This repo contains:
- `Jenkinsfile` : Part 1 Scripted Pipeline (create zip, generate HTML, email)
- `shared-lib/vars/reportGenerator.groovy` : Part 2 shared library to generate the HTML report
- `docker-compose.yml` : Quick environment to run Jenkins + MailHog locally

How to run locally:
1. Start services: `docker compose up -d`
2. Open Jenkins: http://localhost:8080
3. Configure plugins (see instructions in the project README)
4. Add Global Library with Library Path `shared-lib` pointing to this repository
5. Create job under folder `Test`, choose Pipeline -> Scripted Pipeline, paste the Jenkinsfile content (or point to SCM)
6. Run job and check MailHog at http://localhost:8025

Commit plan:
- Commit 1: add `Jenkinsfile` (Part 1)
- Commit 2: add `shared-lib/` and refactor (Part 2)
- Commit 3: add `docker-compose.yml` and README instructions (infrastructure)
