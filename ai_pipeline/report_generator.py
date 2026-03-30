# def generate_report(results):

#     html = "<html><body><h1>AI Failure Analysis Report</h1>"

#     for r in results:
#         html += f"""
#         <h3>{r['test_name']}</h3>
#         <p>Status: {r['status']}</p>
#         <p>Error: {r['error']}</p>
#         <p>Classification: {r['classification']}</p>
#         <p>Flakiness Score: {r['flakiness_score']}</p>
#         <p>Flakiness Level: {r['flakiness_label']}</p>
#         <p>Suggestion: {r['suggestion']}</p>
#         <p>Jira: {r['jira']}</p>
#         """

#         if r["screenshot"]:
#             html += f'<img src="{r["screenshot"]}" width="400"/>'

#         html += "<hr>"

#     html += "</body></html>"

#     with open("ai_report.html", "w") as f:
#         f.write(html)

#     return "ai_report.html"


from datetime import datetime
import os
import json


def generate_report(results, passed, failed, skipped, history):

    timestamp = datetime.utcnow().strftime("%Y-%m-%d_%H-%M-%S")

    os.makedirs("flakiness-report", exist_ok=True)

    summary_file = f"flakiness-report/summary_{timestamp}.html"
    detail_file = f"flakiness-report/detail_{timestamp}.html"
    history_file = f"flakiness-report/history_snapshot_{timestamp}.json"

    # Save snapshot
    with open(history_file, "w") as f:
        json.dump(history, f, indent=2)

    # SUMMARY REPORT
    html = f"""
    <html><body>

    <h2>Execution Summary</h2>
    <table border="1">
        <tr><th>Passed</th><th>Failed</th><th>Skipped</th></tr>
        <tr><td>{passed}</td><td>{failed}</td><td>{skipped}</td></tr>
    </table>

    <br>

    <h2>Flakiness Report</h2>
    <table border="1">
        <tr>
            <th>Test</th>
            <th>Score</th>
            <th>Level</th>
            <th>Total Runs</th>
            <th>Total Failures</th>
        </tr>
    """

    for test, data in history.items():
        html += f"""
        <tr>
            <td>{test}</td>
            <td>{data.get('flakiness_score')}</td>
            <td>{data.get('flakiness_label')}</td>
            <td>{data.get('total_runs')}</td>
            <td>{data.get('total_failures')}</td>
        </tr>
        """

    html += f"""
    </table>

    <br>
    <a href="{os.path.basename(detail_file)}">View Detailed Report</a>

    </body></html>
    """

    with open(summary_file, "w") as f:
        f.write(html)

    # DETAILED REPORT
    detail_html = """
    <html><body>
    <h2>Detailed Failure Analysis</h2>
    <table border="1">
    <tr>
        <th>Test</th>
        <th>Error</th>
        <th>Classification</th>
        <th>Reason</th>
        <th>Fix</th>
        <th>Flakiness</th>
    </tr>
    """

    for r in results:
        detail_html += f"""
        <tr>
            <td>{r['test_name']}</td>
            <td>{r['error']}</td>
            <td>{r['classification']}</td>
            <td>{r['reason']}</td>
            <td>{r['fix']}</td>
            <td>{r['flakiness_score']} ({r['flakiness_label']})</td>
        </tr>
        """

    detail_html += "</table></body></html>"

    with open(detail_file, "w") as f:
        f.write(detail_html)

    return summary_file
