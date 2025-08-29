def call(List stageSummaries = []) {
    def rows = ''
    for (s in stageSummaries) {
        rows += "<tr><td>${s.name}</td><td>${s.result}</td><td>${s.durationMs} ms</td><td>${s.comment}</td></tr>\n"
    }
    return """
    <html>
    <head><title>Shared Lib Build Report</title>
      <style>
        table { border-collapse: collapse; width: 100%; }
        td, th { border: 1px solid #ddd; padding: 8px; }
        th { background: #f2f2f2; }
      </style>
    </head>
    <body>
      <h2>Shared Library Build Report</h2>
      <table>
        <thead><tr><th>Stage</th><th>Result</th><th>Duration</th><th>Comment</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </body>
    </html>
    """
}
