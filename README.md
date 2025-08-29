# Task: Jenkins build + shared library (Satyam Panda)

This repo contains:

* `Jenkinsfile` : Part 1 Scripted Pipeline (create zip, generate HTML, email)
* `shared-lib/vars/reportGenerator.groovy` : Part 2 shared library to generate the HTML report
* `docker-compose.yml` : Quick environment to run Jenkins + MailHog locally

How to run locally:

1. Start services: `docker compose up -d`
2. Open Jenkins: [http://localhost:8080](http://localhost:8080)
3. Configure plugins (see instructions in the project README)
4. Add Global Library with Library Path `shared-lib` pointing to this repository
5. Create job under folder `Test`, choose Pipeline -> Scripted Pipeline, paste the Jenkinsfile content (or point to SCM)
6. Run job and check MailHog at [http://localhost:8025](http://localhost:8025)

Commit plan:

* Commit 1: add `Jenkinsfile` (Part 1)
* Commit 2: add `shared-lib/` and refactor (Part 2)
* Commit 3: add `docker-compose.yml` and README instructions (infrastructure)

---

## How to reproduce (quick)

1. Start services:

   ```bash
   docker compose up -d
   ```

2. Open Jenkins: [http://localhost:8080](http://localhost:8080)

3. Unlock Jenkins with the initial admin password:

   ```bash
   # in Git Bash use winpty if needed:
   winpty docker exec -it satyampanda-jenkins-1 cat /var/jenkins_home/secrets/initialAdminPassword
   ```

4. Install plugins (**Manage Jenkins → Manage Plugins → Available**):

   * Pipeline Utility Steps
   * HTML Publisher
   * Email Extension (email-ext)
   * Folders
   * Role-based Authorization Strategy

5. Configure SMTP (**Manage Jenkins → Configure System**):

   * SMTP server: `mailhog`
   * SMTP port: `1025` (or `2025` if you changed it)

6. Add **Global Pipeline Library** (**Manage Jenkins → Configure System → Global Trusted Pipeline Libraries**):

   * Name: `shared-lib`
   * Retrieval method: `Modern SCM → Git`
   * Project repository: `https://gitlab.com/gl-basecamp/interview/irc257610/satyam.panda.git`
   * Default Version: `task/satyam`
   * Library Path: `shared-lib`
   * Credentials: (add GitLab Personal Access Token if repo is private)

7. Create folder `Test` (**New Item → Folder**).

8. Assign anonymous read to Test (**Manage Jenkins → Manage and Assign Roles**):

   * Create item role `test-folder` with pattern `^Test.*$` and allow **Job → Read**
   * Assign **anonymous** to that role in **Assign Roles**

9. Create Pipeline job `report-job` inside `Test` and paste the Jenkinsfile (Scripted Pipeline).

10. Run **Build Now**. Check:

* Artifacts
* Build Report
* MailHog [http://localhost:8025](http://localhost:8025)

---

👉 After adding this, commit and push:

```bash
git add README.md
git commit -m "docs: add quick reproduction steps for Jenkins + shared-lib"
git push origin task/satyam
```

---

