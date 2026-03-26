def generate_report(results):

    html = "<html><body><h1>AI Failure Analysis Report</h1>"

    for r in results:
        html += f"""
        <h3>{r['test_name']}</h3>
        <p>Status: {r['status']}</p>
        <p>Error: {r['error']}</p>
        <p>Classification: {r['classification']}</p>
        <p>Flakiness Score: {r['flakiness_score']}</p>
        <p>Flakiness Level: {r['flakiness_label']}</p>
        <p>Suggestion: {r['suggestion']}</p>
        <p>Jira: {r['jira']}</p>
        """

        if r["screenshot"]:
            html += f'<img src="{r["screenshot"]}" width="400"/>'

        html += "<hr>"

    html += "</body></html>"

    with open("ai_report.html", "w") as f:
        f.write(html)

    return "ai_report.html"
