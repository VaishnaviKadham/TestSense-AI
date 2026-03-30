import json
import os
from datetime import datetime

HISTORY_FILE = "ai_pipeline/history.json"


def load_history():
    if not os.path.exists(HISTORY_FILE):
        return {}
    with open(HISTORY_FILE, "r") as f:
        return json.load(f)


def save_history(history):
    with open(HISTORY_FILE, "w") as f:
        json.dump(history, f, indent=2)


def calculate_flakiness(history):
    total = history["total_runs"]
    fails = history["total_failures"]

    score = fails / total if total > 0 else 0

    if score == 0:
        label = "STABLE"
    elif score < 0.3:
        label = "LOW"
    elif score < 0.7:
        label = "MEDIUM"
    else:
        label = "HIGH"

    return round(score, 2), label


def update_flakiness(test_name, status, error="", logs="", classification="", reason=""):
    history = load_history()

    if test_name not in history:
        history[test_name] = {
            "history": [],
            "total_runs": 0,
            "total_failures": 0,
            "logs": [],
            "last_updated": ""
        }

    record = history[test_name]

    # Update counts
    record["history"].append(status)
    record["total_runs"] += 1

    if status != "PASS":
        record["total_failures"] += 1

        # SAFE LOG HANDLING ✅
        if logs:
            record["logs"].append({
                "error": error,
                "classification": classification,
                "reason": reason,
                "timestamp": datetime.utcnow().isoformat()
            })

            # FIXED BUG (was [-5])
            record["logs"] = record["logs"][-5:]

    record["last_updated"] = datetime.utcnow().isoformat()

    score, label = calculate_flakiness(record)
    record["flakiness_score"] = score
    record["flakiness_label"] = label

    save_history(history)

    return {"score": score, "label": label}
