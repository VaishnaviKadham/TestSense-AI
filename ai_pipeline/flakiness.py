import json
import os

HISTORY_FILE = "ai_pipeline/history.json"

def update_flakiness(test_name, failed):

    if not os.path.exists(HISTORY_FILE):
        with open(HISTORY_FILE, "w") as f:
            json.dump({}, f)

    with open(HISTORY_FILE, "r") as f:
        data = json.load(f)

    if test_name not in data:
        data[test_name] = {"runs": 0, "failures": 0}

    data[test_name]["runs"] += 1
    if failed:
        data[test_name]["failures"] += 1

    with open(HISTORY_FILE, "w") as f:
        json.dump(data, f, indent=2)

    failure_rate = data[test_name]["failures"] / data[test_name]["runs"]
    return round(failure_rate * 100, 2)
