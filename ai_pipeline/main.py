from parser import parse_test_results
from classifier import classify_failure
from jira_integration import create_jira_bug
from flakiness import update_flakiness
from report_generator import generate_report
from notifier import send_email, send_teams

def run():

    tests = parse_test_results()
    results = []

    for t in tests:

        if t["status"] == "PASS":
            update_flakiness(t["test_name"], False)
            continue

        classification_output = classify_failure(
            t["error"], t.get("logs", "")
        )

        classification = classification_output.lower()

        jira = None
        if "code bug" in classification:
            jira = create_jira_bug(
                t["test_name"], t["error"], classification_output
            )

        flakiness = update_flakiness(t["test_name"], True)

        results.append({
            "test_name": t["test_name"],
            "status": t["status"],
            "error": t["error"],
            "classification": classification,
            "suggestion": classification_output,
            "jira": jira,
            "flakiness": flakiness,
            "screenshot": t["screenshot"]
        })

    report = generate_report(results)

    send_email(report)
    # send_teams()


if __name__ == "__main__":
    run()
