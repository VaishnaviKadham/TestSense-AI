from parser import parse_test_results
from classifier import classify_failures_batch
from jira_integration import create_jira_bug
from flakiness import update_flakiness
from report_generator import generate_report
from notifier import send_email


def run():

    tests = parse_test_results()

    failures = []
    results = []

    # STEP 1: Separate failures
    for t in tests:
        if t["status"] == "PASS":
            update_flakiness(t["test_name"], False)
        else:
            failures.append(t)

    if not failures:
        print("No failed/skipped tests")
        return

    # STEP 2: Single LLM call
    ai_results = classify_failures_batch(failures)

    # Convert to map for easy lookup
    ai_map = {r["test_name"]: r for r in ai_results}

    # STEP 3: Merge results
    for t in failures:

        ai = ai_map.get(t["test_name"], {})

        classification = ai.get("classification", "UNKNOWN")
        reason = ai.get("reason", "")
        fix = ai.get("fix", "")

        full_output = f"""
Classification: {classification}
Reason: {reason}
Fix: {fix}
"""

        jira = None
        if classification == "CODE_BUG":
            jira = create_jira_bug(
                t["test_name"], t["error"], full_output
            )

        flakiness = update_flakiness(t["test_name"], True)

        results.append({
            "test_name": t["test_name"],
            "status": t["status"],
            "error": t["error"],
            "classification": classification.lower(),
            "suggestion": full_output,
            "jira": jira,
            "flakiness": flakiness,
            "screenshot": t["screenshot"]
        })

    report = generate_report(results)
    send_email(report)


if __name__ == "__main__":
    run()
