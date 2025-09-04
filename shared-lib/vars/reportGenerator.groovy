// shared-lib/vars/reportGenerator.groovy
// Usage from pipeline:
//   reportGenerator(stageSummaries, env.JOB_NAME, env.BUILD_NUMBER)

def generate(List stageSummaries, String jobName = '', String buildNumber = '') {
    def rows = ''
    for (s in stageSummaries) {
        def comment = s.comment ?: '-'
        rows += "<tr><td>${s.name}</td><td>${s.result}</td><td>${s.durationMs} ms</td><td>${comment}</td></tr>\n"
    }

    def title = "Build Report - ${jobName ?: 'unknown'} #${buildNumber ?: '0'}"

    return """<!DOCTYPE html>
<html>
<head><meta charset="utf-8"/><title>${title}</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 10px; }
    table { border-collapse: collapse; width: 100%; }
    td, th { border: 1px solid #ddd; padding: 8px; }
    th { background: #f2f2f2; text-align: left; }
  </style>
</head>
<body>
  <h2>${title}</h2>
  <table>
    <thead><tr><th>Stage</th><th>Result</th><th>Duration</th><th>Comment</th></tr></thead>
    <tbody>
      ${rows}
    </tbody>
  </table>
</body>
</html>"""
}

// Allow calling as reportGenerator(stageSummaries, jobName, buildNumber)
def call(List stageSummaries = [], String jobName = '', String buildNumber = '') {
    return generate(stageSummaries, jobName, buildNumber)
}
