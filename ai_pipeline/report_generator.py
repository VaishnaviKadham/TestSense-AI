
from datetime import datetime
import os
import json


def generate_report(results, passed, failed, skipped, history):

    timestamp = datetime.utcnow().strftime("%Y-%m-%d_%H-%M-%S")

    os.makedirs("flakiness-reports", exist_ok=True)

    main_file = f"flakiness-reports/flakiness_{timestamp}.html"

    testng_link = str(
    max(Path("test-results").glob("run-*"), key=lambda p: int(p.name.split("-")[-1]))
    / "surefire-reports" / "index.html"
    )

    html = f"""
    <html>
    <head>
    <style>
        body {{ font-family: Arial; background:#f4f6f8; }}
        h1 {{ color:#2c3e50; }}
        table {{
            border-collapse: collapse;
            width: 100%;
            background: white;
        }}
        th {{
            background:#34495e;
            color:white;
            padding:10px;
        }}
        td {{
            padding:10px;
            border-bottom:1px solid #ddd;
        }}
        tr:hover {{ background:#f1f1f1; }}
        .high {{ color:red; font-weight:bold; }}
        .medium {{ color:orange; }}
        .low {{ color:green; }}
        .btn {{
            background:#3498db;
            color:white;
            padding:8px 12px;
            text-decoration:none;
            border-radius:5px;
        }}
    </style>
    </head>

    <body>

    <h1>🚀 AI Flakiness Dashboard</h1>

    <h2>Execution Summary</h2>
    <table>
        <tr><th>Passed</th><th>Failed</th><th>Skipped</th></tr>
        <tr><td>{passed}</td><td>{failed}</td><td>{skipped}</td></tr>
    </table>

    <br>

    <a class="btn" href="{testng_link}">🔗 Open TestNG Report</a>

    <h2>Flakiness Overview</h2>
    <table>
    <tr>
        <th>Test</th>
        <th>Score</th>
        <th>Level</th>
        <th>Total Runs</th>
        <th>Failures</th>
    </tr>
    """

    for test, data in history.items():

        label_class = data.get("flakiness_label", "").lower()

        html += f"""
        <tr>
            <td>{test}</td>
            <td>{data.get('flakiness_score')}</td>
            <td class="{label_class}">{data.get('flakiness_label')}</td>
            <td>{data.get('total_runs')}</td>
            <td>{data.get('total_failures')}</td>
        </tr>
        """

    # ✅ UPDATED TABLE (ADDED CLASSIFICATION COLUMN)
    html += """
    </table>
    <br>
    <h2>Failure Details</h2>
    <table>
    <tr>
        <th>Test</th>
        <th>Error</th>
        <th>Classification</th>
        <th>AI Reason</th>
        <th>Fix</th>
    </tr>
    """

    for r in results:
        html += f"""
        <tr>
            <td>{r['test_name']}</td>
            <td>{r['error']}</td>
            <td>{r['classification']}</td>
            <td>{r['reason']}</td>
            <td>{r['fix']}</td>
        </tr>
        """

    html += "</table></body></html>"

    with open(main_file, "w") as f:
        f.write(html)

    # Save snapshot
    with open(f"flakiness-reports/history_{timestamp}.json", "w") as f:
        json.dump(history, f, indent=2)

    return main_file
