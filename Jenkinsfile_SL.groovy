@Library('interview-lib') _
/*
  Jenkinsfile_SL.groovy
  Example Scripted Pipeline that uses shared-lib/reportGenerator
*/
node {
    def stageSummaries = []
    def recordSummary = { String name, long duration, String result, String comment ->
        stageSummaries << [name: name, durationMs: duration, result: result, comment: comment]
    }

    try {
        stage('Create artifact') {
            def start = System.currentTimeMillis()
            writeFile file: 'artifacts/sample.txt', text: 'Hello from Satyam Panda - sample file (SL)'
            if (fileExists('README.md')) {
                writeFile file: 'artifacts/README.md', text: readFile('README.md')
            }
            zip zipFile: 'artifacts/my-archive.zip', dir: 'artifacts'
            archiveArtifacts artifacts: 'artifacts/my-archive.zip'
            recordSummary('Create and upload zip', System.currentTimeMillis() - start, 'SUCCESS', 'Created artifacts and zipped')
        }

        stage('Generate Report (Shared Lib)') {
            def start = System.currentTimeMillis()
            // call the shared library to generate the HTML content
            def html = reportGenerator(stageSummaries, env.JOB_NAME, env.BUILD_NUMBER)
            writeFile file: 'report.html', text: html

            publishHTML target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: '.', reportFiles: 'report.html', reportName: 'Build Report (Shared Lib)']

            recordSummary('Generate HTML report', System.currentTimeMillis() - start, 'SUCCESS', 'Generated report.html via shared library')
        }

        stage('Email (Shared Lib)') {
            def start = System.currentTimeMillis()
            emailext mimeType: 'text/html',
                    body: '${FILE, path="report.html"}',
                    subject: "${currentBuild.currentResult ?: 'SUCCESS'} : ${env.JOB_NAME}",
                    to: 'test@local.test'
            recordSummary('Send the report via email', System.currentTimeMillis() - start, 'SUCCESS', 'Email sent (check MailHog)')
        }

    } catch (err) {
        currentBuild.result = 'FAILURE'
        error("Build failed: ${err.getMessage()}")
    } finally {
        writeFile file: 'stage-summaries.json', text: groovy.json.JsonOutput.toJson(stageSummaries)
        archiveArtifacts artifacts: 'stage-summaries.json'
    }
}
