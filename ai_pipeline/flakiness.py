# # import json
# # import os
# # import logging
# # from datetime import datetime

# # logger = logging.getLogger(__name__)

# # HISTORY_FILE = "ai_pipeline/history.json"

# # MAX_RUNS = 10          # only last 10 runs
# # MIN_RUNS_REQUIRED = 3  # minimum runs required


# # # ─────────────────────────────────────────────
# # # FILE HANDLING
# # # ─────────────────────────────────────────────

# # def load_history():
# #     if not os.path.exists(HISTORY_FILE):
# #         return {}

# #     try:
# #         with open(HISTORY_FILE, "r") as f:
# #             return json.load(f)
# #     except:
# #         return {}


# # def save_history(data):
# #     os.makedirs(os.path.dirname(HISTORY_FILE), exist_ok=True)

# #     with open(HISTORY_FILE, "w") as f:
# #         json.dump(data, f, indent=2)


# # # ─────────────────────────────────────────────
# # # SCORING LOGIC
# # # ─────────────────────────────────────────────

# # def compute_flakiness(results):
# #     n = len(results)

# #     #  Minimum data check
# #     if n < MIN_RUNS_REQUIRED:
# #         return {
# #             "score": 0.0,
# #             "label": "insufficient_data"
# #         }

# #     # ── FAILURE RATE ──
# #     failure_rate = results.count("fail") / n

# #     # ── ALTERNATION RATE ──
# #     transitions = sum(
# #         1 for i in range(1, n) if results[i] != results[i - 1]
# #     )
# #     alternation_rate = transitions / (n - 1) if n > 1 else 0

# #     # ── RECENCY WEIGHT ──
# #     weighted_failures = 0
# #     weighted_total = 0
# #     midpoint = n // 2

# #     for i, r in enumerate(results):
# #         weight = 2 if i >= midpoint else 1
# #         weighted_total += weight
# #         if r == "fail":
# #             weighted_failures += weight

# #     recency_score = (
# #         weighted_failures / weighted_total
# #         if weighted_total > 0 else 0
# #     )

# #     # ── FINAL SCORE ──
# #     score = (
# #         failure_rate * 4 +
# #         alternation_rate * 3.5 +
# #         recency_score * 2.5
# #     )

# #     score = round(min(score, 10), 2)

# #     return {
# #         "score": score,
# #         "label": get_label(score)
# #     }


# # def get_label(score):
# #     if score <= 1.0:
# #         return "stable"
# #     elif score <= 3.0:
# #         return "low"
# #     elif score <= 5.5:
# #         return "medium"
# #     elif score <= 7.5:
# #         return "high"
# #     else:
# #         return "critical"


# # # ─────────────────────────────────────────────
# # # MAIN FUNCTION (USED IN main.py)
# # # ─────────────────────────────────────────────

# # def update_flakiness(test_name, failed):
# #     data = load_history()

# #     if test_name not in data:
# #         data[test_name] = {
# #             "history": [],
# #             "total_runs": 0,
# #             "total_failures": 0
# #         }

# #     record = data[test_name]

# #     result = "fail" if failed else "pass"

# #     #  Update rolling history (last 10 runs only)
# #     record["history"].append(result)
# #     record["history"] = record["history"][-MAX_RUNS:]

# #     #  Update counters
# #     record["total_runs"] += 1
# #     if failed:
# #         record["total_failures"] += 1

# #     # Compute score
# #     stats = compute_flakiness(record["history"])

# #     # Timestamp
# #     record["last_updated"] = datetime.utcnow().isoformat()

# #     save_history(data)

# #     return stats

# import json
# import os
# from datetime import datetime

# HISTORY_FILE = "ai_pipeline/history.json"
# MAX_RUNS = 10
# MIN_RUNS_REQUIRED = 3


# def load_history():
#     if not os.path.exists(HISTORY_FILE):
#         return {}
#     try:
#         with open(HISTORY_FILE, "r") as f:
#             return json.load(f)
#     except:
#         return {}


# def save_history(data):
#     os.makedirs(os.path.dirname(HISTORY_FILE), exist_ok=True)
#     with open(HISTORY_FILE, "w") as f:
#         json.dump(data, f, indent=2)


# def compute_flakiness(results):
#     n = len(results)

#     if n < MIN_RUNS_REQUIRED:
#         return {"score": 0.0, "label": "insufficient_data"}

#     failure_rate = results.count("fail") / n

#     transitions = sum(
#         1 for i in range(1, n) if results[i] != results[i - 1]
#     )
#     alternation_rate = transitions / (n - 1)

#     weighted_failures = 0
#     weighted_total = 0
#     midpoint = n // 2

#     for i, r in enumerate(results):
#         weight = 2 if i >= midpoint else 1
#         weighted_total += weight
#         if r == "fail":
#             weighted_failures += weight

#     recency_score = weighted_failures / weighted_total

#     score = (
#         failure_rate * 4 +
#         alternation_rate * 3.5 +
#         recency_score * 2.5
#     )

#     score = round(min(score, 10), 2)

#     return {"score": score, "label": get_label(score)}


# def get_label(score):
#     if score <= 1:
#         return "stable"
#     elif score <= 3:
#         return "low"
#     elif score <= 5.5:
#         return "medium"
#     elif score <= 7.5:
#         return "high"
#     else:
#         return "critical"


# def update_flakiness(test_name, status, error="", logs="", classification="", reason=""):

#     data = load_history()

#     # First time test appears
#     if test_name not in data:
#         data[test_name] = {
#             "history": ["pass"] * (MIN_RUNS_REQUIRED - 1),
#             "total_runs": MIN_RUNS_REQUIRED - 1,
#             "total_failures": 0,
#             "logs": [],
#             "last_failure_reason": ""
#         }

#     record = data[test_name]

#     result = "fail" if status in ["FAIL", "SKIPPED"] else "pass"

#     record["history"].append(result)
#     record["history"] = record["history"][-MAX_RUNS:]

#     record["total_runs"] += 1

#     if result == "fail":
#         record["total_failures"] += 1
#         record["last_failure_reason"] = reason

#         record["logs"].append({
#             "timestamp": datetime.utcnow().isoformat(),
#             "error": error,
#             "logs": logs,
#             "classification": classification
#         })

#        # if not isinstance(record.get("logs"), list):
#            # record["logs"] = []

#     record["logs"] = record["logs"][-5:] 
#     stats = compute_flakiness(record["history"])

#     record["flakiness_score"] = stats["score"]
#     record["flakiness_label"] = stats["label"]
#     record["last_updated"] = datetime.utcnow().isoformat()

#     save_history(data)

#     return stats


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
