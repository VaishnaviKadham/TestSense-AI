import os
import time
import json
import re
import logging
from typing import List, Dict

from google import genai

logger = logging.getLogger(__name__)

# Gemini client (new SDK)
client = genai.Client(
    api_key=os.getenv("GEMINI_API_KEY")
)

# Use stable free model
MODEL = "gemini-2.5-flash-lite"

# Full system prompt (restored)
SYSTEM_PROMPT = """You are an expert SDET and SRE.

Classify failures into:
- CODE_BUG
- FLAKY_TEST
- ENV_ISSUE
- DATA_ISSUE
- INFRA_FAILURE

Rules:
- Use strong evidence from error/logs
- Do NOT guess
- Keep reason short (1 line)
- Keep fix actionable (1 line)
- Return ONLY valid JSON
"""


def build_prompt(failures: List[Dict]) -> str:
    prompt = SYSTEM_PROMPT + "\n\n"
    prompt += "Analyze the following test failures:\n\n"

    for i, f in enumerate(failures):
        prompt += f"""
Test {i+1}
Name: {f['test_name']}

Error:
{f['error'][:400]}

Logs:
{f.get('logs','')[:400]}
"""

    prompt += """
Return JSON:

[
  {
    "test_name": "...",
    "classification": "CODE_BUG | FLAKY_TEST | ENV_ISSUE | DATA_ISSUE | INFRA_FAILURE",
    "reason": "short reason",
    "fix": "short fix"
  }
]
"""
    return prompt


def classify_failures_batch(failures: List[Dict], retries=3):

    if not failures:
        return []

    # keep batch small for Gemini stability
    failures = failures[:5]

    prompt = build_prompt(failures)

    for attempt in range(retries):
        try:
            logger.info(f"Gemini call attempt {attempt+1}")
            logger.info(f"Prompt size: {len(prompt)} chars")

            response = client.models.generate_content(
                model=MODEL,
                contents=prompt
            )

            raw = response.text

            return extract_json(raw)

        except Exception as e:
            logger.error(f"Attempt {attempt+1} failed: {e}")
            time.sleep(2 ** attempt)

    return fallback(failures)


def extract_json(text: str):

    # Try strict JSON first
    try:
        return json.loads(text)
    except:
        pass

    # Try extracting JSON array
    match = re.search(r"\[[\s\S]+\]", text)
    if match:
        try:
            return json.loads(match.group(0))
        except Exception as e:
            logger.error(f"JSON parsing failed: {e}")

    raise ValueError("Invalid JSON from model")


def fallback(failures: List[Dict]):

    return [
        {
            "test_name": f["test_name"],
            "classification": "UNKNOWN",
            "reason": "AI failed",
            "fix": "Manual analysis required"
        }
        for f in failures
    ]

