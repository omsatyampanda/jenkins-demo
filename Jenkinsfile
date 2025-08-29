// Jenkinsfile (Scripted Pipeline) - Part 1
node {
    def stageSummaries = []
    def recordSummary = { String name, long duration, String result, String comment ->
        stageSummaries << [name: name, durationMs: duration, result: result, comment: comment]
    }

    try {
        stage('Create and upload zip') {
    def start = System.currentTimeMillis()
    sh '''
        mkdir -p artifacts
        echo "Hello from Satyam Panda - sample file" > artifacts/sample.txt
        if [ -f README.md ]; then cp README.md artifacts/README.md; fi
        rm -f artifacts/my-archive.zip   # remove old zip if exists
    '''
    zip zipFile: 'artifacts/my-archive.zip', dir: 'artifacts'
    archiveArtifacts artifacts: 'artifacts/my-archive.zip'
    recordSummary('Create and upload zip', System.currentTimeMillis() - start, currentBuild.currentResult ?: 'SUCCESS', 'Created artifacts and zipped')
}
        stage('Generate HTML report') {
            def start = System.currentTimeMillis()
            def rows = ''
            for (s in stageSummaries) {
                rows += "<tr><td>${s.name}</td><td>${s.result}</td><td>${s.durationMs} ms</td><td>${s.comment}</td></tr>\n"
            }
            rows += "<tr><td>Generate HTML report</td><td>SUCCESS</td><td>--</td><td>Generated report</td></tr>\n"

            def html = """
            <html>
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
                <tbody>${rows}</tbody>
              </table>
            </body>
            </html>
            """
            writeFile file: 'report.html', text: html
            publishHTML target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: '.', reportFiles: 'report.html', reportName: 'Build Report']
            recordSummary('Generate HTML report', System.currentTimeMillis() - start, 'SUCCESS', 'Generated report.html and published')
        }

        stage('Send the report via email') {
            def start = System.currentTimeMillis()
            emailext mimeType: 'text/html',
                     body: '${FILE, path="report.html"}',
                     subject: "${currentBuild.currentResult} : ${env.JOB_NAME}",
                     to: 'test@example.com'
            recordSummary('Send the report via email', System.currentTimeMillis() - start, 'SUCCESS', 'Email sent (check MailHog)')
        }

    } catch (err) {
        currentBuild.result = 'FAILURE'
        throw err
    } finally {
        writeFile file: 'stage-summaries.json', text: groovy.json.JsonOutput.toJson(stageSummaries)
        archiveArtifacts artifacts: 'stage-summaries.json'
    }
}
