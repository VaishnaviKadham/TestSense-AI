import requests
import os

def send_teams():

    webhook = os.getenv("TEAMS_WEBHOOK")

    payload = {
        "text": "AI Test Analysis Completed. Report Generated."
    }

    requests.post(webhook, json=payload)
