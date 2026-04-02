from parser import parse_test_results
from classifier import classify_failures_batch
from jira_integration import create_jira_bug
from flakiness import update_flakiness, load_history
from report_generator import generate_report
from notifier import send_email


def run():

    tests = parse_test_results()

    pass_count = 0
    fail_count = 0
    skip_count = 0

    failures = []
    results = []

    for t in tests:

        if t["status"] == "PASS":
            pass_count += 1
            update_flakiness(t["test_name"], "PASS")

        else:
            if t["status"] == "FAIL":
                fail_count += 1
            else:
                skip_count += 1

            failures.append(t)

    ai_results = []

    for f in failures:
        result = classify_failures_batch([f])  # one-by-one processing
        if result:
            ai_results.extend(result)

    ai_map = {r["test_name"]: r for r in ai_results}

    for t in failures:

        ai = ai_map.get(t["test_name"], {})

        classification = ai.get("classification", "UNKNOWN")
        reason = ai.get("reason", "")
        fix = ai.get("fix", "")

        jira = None
        if classification == "CODE_BUG":
            jira = create_jira_bug(t["test_name"], t["error"], reason)

        flaky = update_flakiness(
            t["test_name"],
            t["status"],
            t["error"],
            t.get("logs", ""),
            classification,
            reason
        )

        results.append({
            "test_name": t["test_name"],
            "status": t["status"],
            "error": t["error"],
            "classification": classification,   # ✅ IMPORTANT
            "reason": reason,
            "fix": fix,
            "jira": jira,
            "flakiness_score": flaky["score"],
            "flakiness_label": flaky["label"]
        })

    history = load_history()

    report_file = generate_report(
        results, pass_count, fail_count, skip_count, history
    )

    send_email(report_file)


if __name__ == "__main__":
    run()

