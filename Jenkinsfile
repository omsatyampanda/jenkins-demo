node {
  // ---- tiny helper to measure stage timing and result ----
  def stages = []  // each item: [name: "...", result: "SUCCESS/FAILURE/UNSTABLE", ms: 1234, comment: "...."]

  def runStage = { String title, Closure body ->
    def start = System.currentTimeMillis()
    def status = "SUCCESS"
    def comment = ""
    try {
      stage(title) {
        body()
      }
    } catch (e) {
      status = "FAILURE"
      comment = "Exception: ${e.getMessage()}"
      currentBuild.result = 'FAILURE'
      echo "Stage '${title}' failed: ${e}"
    } finally {
      def end = System.currentTimeMillis()
      stages << [name: title, result: status, ms: (end - start), comment: comment]
    }
  }

  // -------- STAGE 1: Create zip artifact --------
  runStage('Create ZIP') {
    sh 'mkdir -p build'
    // create a zip from artifacts/
    // requires Pipeline Utility Steps plugin for 'zip' step
    zip zipFile: 'build/my-artifacts.zip', archive: true, dir: 'artifacts'
    echo "ZIP created and archived."
  }

  // -------- STAGE 2: Generate HTML report --------
  runStage('Generate Report') {
    def rows = stages.collect { s ->
      def seconds = (s.ms / 1000.0).toString()
      """<tr>
           <td>${s.name}</td>
           <td>${s.result}</td>
           <td>${seconds}s</td>
           <td>${s.comment ?: '-'}</td>
         </tr>"""
    }.join("\n")

    def html = """<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Build Report - ${env.JOB_NAME} #${env.BUILD_NUMBER}</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    table { border-collapse: collapse; width: 100%; }
    th, td { border: 1px solid #ddd; padding: 8px; }
    th { background: #f2f2f2; text-align: left; }
  </style>
</head>
<body>
  <h2>Build Report: ${env.JOB_NAME} #${env.BUILD_NUMBER}</h2>
  <table>
    <thead><tr><th>Stage</th><th>Result</th><th>Time</th><th>Comment</th></tr></thead>
    <tbody>${rows}</tbody>
  </table>
</body>
</html>"""

    writeFile file: 'build/report.html', text: html

    publishHTML(target: [
      reportDir: 'build',
      reportFiles: 'report.html',
      reportName: 'HTML Report',
      keepAll: true,
      alwaysLinkToLastBuild: true
    ])
  }

  // -------- STAGE 3: Send email via Email Extension --------
  runStage('Email Report') {
    // Sends HTML body by embedding the file
    emailext(
      subject: "Build ${currentBuild.currentResult} : ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      mimeType: 'text/html',
      body: '${FILE, path="build/report.html"}',
      to: 'test@local.test' // goes to MailHog inbox
    )
    echo "Email sent to MailHog."
  }
}
