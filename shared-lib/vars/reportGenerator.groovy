def generate(List stages, String jobName, String buildNumber) {
  def rows = stages.collect { s ->
    """<tr>
         <td>${s.name}</td>
         <td>${s.result}</td>
         <td>${s.ms / 1000.0}s</td>
         <td>${s.comment ?: '-'}</td>
       </tr>"""
  }.join("\n")

  def tmpl = libraryResource('report-template.html')
  return tmpl
    .replace('@@TITLE@@', "Build Report - ${jobName} #${buildNumber}")
    .replace('@@HEADING@@', "Build Report: ${jobName} #${buildNumber}")
    .replace('@@ROWS@@', rows)
}

def call(List stages, String jobName, String buildNumber) {
  return generate(stages, jobName, buildNumber)
}
