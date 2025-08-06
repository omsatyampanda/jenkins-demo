## Task Overview

The assignment consists of three progressively more complex parts.  You may
complete as many as you feel comfortable with.  All code must be
implemented using **Scripted Pipeline** (not Declarative) and Groovy.

### Part 1 – Basic Functionality

1. **Set up a new Jenkins instance.**  Use a container platform such as
   Docker or Podman to start a fresh Jenkins controller.  The official
   `jenkins/jenkins:lts` image is a good base.  Expose the web UI on a
   predictable port (e.g. 8080) and persist data in a volume so your
   configuration and plugins survive restarts.

2. **Install required plugins.**  Through the *Manage Jenkins → Manage
   Plugins* UI, install at least the following plugins:

   - **Folders** and **Folder‑based Authorisation Strategy** to create
     folder hierarchies and assign permissions.
   - **Jenkins Shared Library** (a built‑in capability) and **Pipeline Utility
     Steps**.  The `zip` step from Pipeline Utility Steps creates zip
     archives inside the workspace.  Its help page notes that the step
     “**creates a zip file of content in the workspace**”
     and the `zipFile` parameter defines “**the name/path of the zip file to
     create**”.
   - **HTML Publisher** (used for publishing HTML reports).
   - **Email Extension (email‑ext)**.  This plugin exposes an `emailext`
     pipeline step which can send emails with custom subjects and bodies.  Its
     documentation shows a simple invocation:

     ```groovy
     emailext body: 'Test Message',
         subject: 'Test Subject',
         to: 'test@example.com'
     ```

     and explains that you can pass additional `recipientProviders` or
     customise other fields.  Make sure you
     configure SMTP settings under *Manage Jenkins → Configure System* so
     your Jenkins instance can send mail.  You might use a personal
     SMTP server or a local mail catcher.

3. **Create the `Test` folder and set permissions.**  Use the Folders
   plugin to create a folder named `Test` at the root of your Jenkins
   controller.  Configure the folder to grant **read access** to the
   anonymous user using the Folder‑based Authorisation Strategy.  This will
   allow anyone to view job results without login while still limiting
   write access.

4. **Implement a Scripted Pipeline job.**  Inside the `Test` folder,
   create a new *Pipeline* job and provide a Groovy script implementing
   three stages:

   1. **Create and upload a zip file.**  The first stage should prepare
      some content (e.g. sample text files or the source code of this
      repository), then call the `zip` step to create an archive.  Use
      the `zipFile` parameter to specify the output path (e.g.
      `artifacts/my-archive.zip`).  Store the resulting archive as a build
      artifact so it can be downloaded from the Jenkins UI.

   2. **Generate an HTML report.**  The second stage must produce an
      `report.html` file containing a table summarising the build.  Each
      row of the table should list:

      - **Stage result:** success, unstable or failure.
      - **Execution time:** measure how long each stage took (you can
        record timestamps before and after each stage using
        `System.currentTimeMillis()`).
      - **Comment:** any notes or diagnostics you wish to include.

      You may construct the HTML manually in Groovy (for example by
      building a list of maps and iterating over it) or use a templating
      library if desired.  Once generated, write the HTML to a file in
      the workspace.  You can also publish it using the **HTML Publisher**
      plugin so it appears in the build’s *Published HTML Reports*
      section.

   3. **Send the report via email.**  In the final stage, use the
      `emailext` step from the Email Extension plugin to send your
      `report.html` file to yourself or a test recipient.  For HTML
      attachments, set `mimeType: 'text/html'` and reference the
      workspace file using the `${FILE, path="report.html"}` token.  The
      CloudBees knowledge base provides a sample snippet for embedding an
      HTML file into the email body:

      ```groovy
      node {
          // ... generate myfile.html ...
          emailext mimeType: 'text/html',
              body: '${FILE, path="myfile.html"}',
              subject: currentBuild.currentResult + " : " + env.JOB_NAME,
              to: 'example@example.com'
      }
      ```

5. **Demonstrate your solution.**  Commit your pipeline script (`Jenkinsfile`
   or inline script) and any supporting code to the repository.  During the
   interview you will need to run the Jenkins controller, execute the job,
   show the archived zip file, the generated HTML report and the email
   message received.

### Part 2 – Medium Difficulty

Extend your solution from Part 1 by moving the HTML report generation logic
into a **Jenkins Shared Library**.  Shared libraries allow common pipeline
patterns to be centralised and reused.  To complete this part:

1. **Store the shared library in this repository**.  For this
   assignment, you must place your Jenkins shared library within the same
   repository that contains your home test task.  Create a
   dedicated subdirectory (for example `shared-lib/`) at the project root
   and put your library’s `src/`, `vars/` and `resources/` folders inside
   it.  Avoid creating a separate Git repository – our evaluation system
   assumes a single project.  Your library must follow the expected
   directory structure:

   ````
   (root)
   +- src/        # optional Groovy classes, standard Java package structure
   +- vars/       # global variables & functions exposed to pipelines
   +- resources/  # static resource files (templates, etc.)
   ````

   The Jenkins documentation explains that files in the `vars` directory
   become global variables in pipelines.  For example, a file
   `vars/log.groovy` that defines a function `info(message)` can be
   referenced in pipeline scripts as `log.info('hello world')`.
   The `src` directory is added to the classpath and follows standard
   package naming conventions.

2. **Implement a global variable** (e.g. `reportGenerator.groovy`) under
   `vars/` that exposes a function to build the HTML table used in your
   report.  The function should accept a list of stage summaries and
   return a string containing the full HTML document.  You can place
   templates in the `resources/` folder if you prefer using a
   templating engine (e.g. GString templates) instead of constructing
   HTML inline.

3. **Configure Jenkins to load your library.**  In *Manage Jenkins →
   System → Global Pipeline Libraries* (or at the folder level) add a
   library definition.  Give it a name and specify the SCM as this
   repository, then set the **Library Path** to the subdirectory that
   contains your library (e.g. `shared-lib`).  This tells Jenkins where
   to find the `src/`, `vars/` and `resources/` folders.  You may choose
   whether the library loads implicitly or requires explicit declaration.
   For explicit usage, add the annotation
   `@Library('your-library') _` at the top of your pipeline script.

4. **Refactor your pipeline.**  Update the pipeline created in Part 1 so
   the HTML report is produced by calling the function in your shared
   library.  This demonstrates your ability to encapsulate and reuse
   pipeline code across jobs.

5. **Demonstrate your solution.**  Commit the shared library code and the
   refactored pipeline to your repositories.  During the interview you
   should show how the library is configured, how it is called from the
   pipeline, and that the functionality remains unchanged.

### Part 3 – Expert Level

For candidates with extensive Jenkins experience, implement the same
report‑generation functionality as a **custom Jenkins plugin**.  Plugin
development allows features to be packaged cleanly and shared across
instances.

1. **Bootstrap the plugin using the Jenkins Maven archetype.**  The
   official developer guide describes how to generate a plugin skeleton.
   From a terminal, run:

   ```sh
   mvn -U archetype:generate -Dfilter=io.jenkins.archetypes:
   ```

   You will be prompted to choose an archetype.  The guide suggests
   selecting the `hello‑world‑plugin` archetype (option 4) and then
   choosing a version.  These steps create a directory containing the
   project layout and build files for a working plugin.

2. **Implement a pipeline step or builder** that encapsulates your report
   logic.  A simple approach is to create a `Step` class that accepts
   parameters (e.g. list of stages and output file name) and implements
   `@DataBoundConstructor` and `@DataBoundSetter` methods so it can be
   configured in both Pipeline and freestyle projects.  The step should
   write the HTML report to the workspace and (optionally) archive it or
   trigger an email.  Follow the Jenkins plugin development guidelines:
   choose a recent Jenkins baseline, avoid deprecated APIs, and provide
   a minimal configuration form.  The tutorial notes that the created
   project can be built with `mvn verify` to produce a `.hpi` file which
   Jenkins can install.

3. **Package and install the plugin.**  Build your plugin with Maven and
   install it into your Jenkins controller (Manage Jenkins → Manage
   Plugins → Advanced → Upload Plugin).  Demonstrate that your new
   pipeline step appears in the Snippet Generator and functions
   correctly in a pipeline job.  Optionally, update your shared
   library or pipeline script to call the new plugin step instead of the
   inline Groovy implementation.

4. **Demonstrate your solution.**  Commit the plugin source code to your
   repository.  During the interview you should show how you built the
   plugin, how to install it and how to use the custom step in a
   pipeline.

## Delivery and Evaluation

Please ensure your repository includes:

1. **Each part of the assignment (Part 1, Part 2 and Part 3) should be
   committed as a separate commit**.  This helps us review your progress
   and understand how the solution evolved.
2. The **README.md** (this file) describing your approach, assumptions, and any
   prerequisites.
3. A **Jenkinsfile** or inline pipeline script implementing Part 1.
4. The **shared library** code (if completing Part 2) inside this
   repository (for example under `shared-lib/`), along with any tests
   or documentation.
5. Plugin source code and packaged `.hpi` file (if completing Part 3).
6. A **`docker-compose.yml`** or equivalent scripts to spin up Jenkins and
   any supporting services (e.g. SMTP test container) to make
   reproduction straightforward.

During the interview you will be assessed on:

* **Correctness and completeness** – Does your solution meet all
  requirements?  Are the zip archive, HTML report and email generated
  correctly?
* **Quality and organisation of code** – Is your Groovy pipeline
  readable, modular and maintainable?  Does your shared library follow
  the recommended directory structure?
* **Attention to security and permissions** – Is the `Test` folder
  configured with appropriate read permissions?  Are secrets (SMTP
  credentials, etc.) handled securely?
* **Demonstration** – Can you start Jenkins, run your job, and walk
  through the artefacts and reports?  If you built a plugin, can you
  show how it integrates into Jenkins?

Good luck, and we look forward to reviewing your solution!
