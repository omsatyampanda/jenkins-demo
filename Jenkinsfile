// Jenkinsfile (Scripted Pipeline) - Part 1 (fixed per review comments)
node {
    def stageSummaries = []
    def recordSummary = { String name, long duration, String result, String comment ->
        stageSummaries << [name: name, durationMs: duration, result: result, comment: comment]
    }

    try {
        stage('Create and upload zip') {
            def start = System.currentTimeMillis()

            // create artifact files using Jenkins helpers (no raw sh)
            writeFile file: 'artifacts/sample.txt', text: 'Hello from Satyam Panda - sample file'
            if (fileExists('README.md')) {
                writeFile file: 'artifacts/README.md', text: readFile('README.md')
            }

            // create zip and archive
            zip zipFile: 'artifacts/my-archive.zip', dir: 'artifacts'
            archiveArtifacts artifacts: 'artifacts/my-archive.zip'

            // record summary for this stage
            recordSummary('Create and upload zip', System.currentTimeMillis() - start, currentBuild.currentResult ?: 'SUCCESS', 'Created artifacts and zipped')
        }

        stage('Generate HTML report') {
            def start = System.currentTimeMillis()

            // build the HTML content
            def html = """<html>
            <head><title>Build Report</title>
                <style>
                  table { border-collapse: collapse; width: 100%; }
                  td, th { border: 1px solid #ddd; padding: 8px; }
                  th { background: #f2f2f2; }
                </style>
            </head>
            <body>
              <h2>Build Report - ${env.JOB_NAME} #${env.BUILD_NUMBER}</h2>
              <table>
                <thead><tr><th>Stage</th><th>Result</th><th>Duration</th><th>Comment</th></tr></thead>
                <tbody><!-- rows will be inserted here --></tbody>
              </table>
            </body>
            </html>"""

            // write file to workspace
            writeFile file: 'report.html', text: html

            // publish HTML (from workspace root)
            publishHTML target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: '.', reportFiles: 'report.html', reportName: 'Build Report']

            // record summary for this stage (so it will appear in stageSummaries)
            recordSummary('Generate HTML report', System.currentTimeMillis() - start, 'SUCCESS', 'Generated report.html and published')
            
            // Now build table rows from stageSummaries (this includes the Generate HTML report entry we just added)
            def rows = ''
            for (s in stageSummaries) {
                rows += "<tr><td>${s.name}</td><td>${s.result}</td><td>${s.durationMs} ms</td><td>${s.comment ?: '-'} </td></tr>\n"
            }

            // combine with the outer HTML and overwrite report.html with rows included
            def finalHtml = readFile('report.html').replaceFirst('<!-- rows will be inserted here -->', rows)
            writeFile file: 'report.html', text: finalHtml
        }

        stage('Send the report via email') {
            def start = System.currentTimeMillis()

            emailext mimeType: 'text/html',
                    body: '${FILE, path="report.html"}',
                    subject: "${currentBuild.currentResult ?: 'SUCCESS'} : ${env.JOB_NAME}",
                    to: 'test@local.test'   // MailHog
            recordSummary('Send the report via email', System.currentTimeMillis() - start, 'SUCCESS', 'Email sent (check MailHog)')
        }

    } catch (err) {
        // mark build failed and throw Jenkins-friendly error
        currentBuild.result = 'FAILURE'
        error("Build failed: ${err.getMessage()}")
    } finally {
        // save stage summaries and archive them so reviewer can inspect
        writeFile file: 'stage-summaries.json', text: groovy.json.JsonOutput.toJson(stageSummaries)
        archiveArtifacts artifacts: 'stage-summaries.json'
    }
}
