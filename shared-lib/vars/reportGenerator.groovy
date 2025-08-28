// Exposed as: reportGenerator.generate(stages, jobName, buildNumber)
def generate(List stages, String jobName, String buildNumber) {
  def rows = stages.collect { s ->
    def seconds = (s.ms / 1000.0).toString()
    """<tr>
         <td>${s.name}</td>
         <td>${s.result}</td>
         <td>${seconds}s</td>
         <td>${s.comment ?: '-'}</td>
       </tr>"""
  }.join("\n")

  // Load template from resources
  def tmpl = libraryResource('report-template.html')
  return tmpl
    .replace('@@TITLE@@', "Build Report - ${jobName} #${buildNumber}")
    .replace('@@HEADING@@', "Build Report: ${jobName} #${buildNumber}")
    .replace('@@ROWS@@', rows)
}

// Allow calling as reportGenerator(stages, job, build)
def call(List stages, String jobName, String buildNumber) {
  return generate(stages, jobName, buildNumber)
}
