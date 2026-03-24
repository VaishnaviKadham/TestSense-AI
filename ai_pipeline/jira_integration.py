import requests
import os

JIRA_URL = os.getenv("JIRA_URL")
EMAIL = os.getenv("JIRA_EMAIL")
API_TOKEN = os.getenv("JIRA_API_TOKEN")

def create_jira_bug(test_name, error, suggestion):

    url = f"{JIRA_URL}/rest/api/3/issue"

    payload = {
        "fields": {
            "project": {"key": "QA"},
            "summary": f"Automation Failure: {test_name}",
            "description": f"{error}\n\n{suggestion}",
            "issuetype": {"name": "Bug"}
        }
    }

    response = requests.post(
        url,
        json=payload,
        auth=(EMAIL, API_TOKEN),
        headers={"Content-Type": "application/json"}
    )

    if response.status_code == 201:
        return response.json()["key"]
    return "Not Created"
