node {
  def stages = []

  def runStage = { String title, Closure body ->
    def start = System.currentTimeMillis()
    def status = "SUCCESS"
    def comment = ""
    try {
      stage(title) { body() }
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

  runStage('Create ZIP') {
    sh 'mkdir -p build'
    zip zipFile: 'build/my-artifacts.zip', archive: true, dir: 'artifacts'
  }

  runStage('Generate Report') {
    def rows = stages.collect { s ->
      """<tr>
           <td>${s.name}</td>
           <td>${s.result}</td>
           <td>${s.ms / 1000.0}s</td>
           <td>${s.comment ?: '-'}</td>
         </tr>"""
    }.join("\n")

    def html = """<!DOCTYPE html>
<html>
<head><meta charset="utf-8"/><title>Build Report</title></head>
<body>
  <h2>Build Report: ${env.JOB_NAME} #${env.BUILD_NUMBER}</h2>
  <table border="1">
    <tr><th>Stage</th><th>Result</th><th>Time</th><th>Comment</th></tr>
    ${rows}
  </table>
</body>
</html>"""

    writeFile file: 'build/report.html', text: html
    publishHTML(target: [reportDir: 'build', reportFiles: 'report.html', reportName: 'HTML Report'])
  }

  runStage('Email Report') {
    emailext(
      subject: "Build ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
      mimeType: 'text/html',
      body: '${FILE, path="build/report.html"}',
      to: 'test@local.test'
    )
  }
}
