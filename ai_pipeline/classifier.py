import os
import time
import json
import re
# import anthropic   # ⛔ Commented out
import logging
from typing import List, Dict

from openai import OpenAI   # Added

logger = logging.getLogger(__name__)

# client = anthropic.Anthropic(
#     api_key=os.getenv("ANTHROPIC_API_KEY")
# )

# penAI client
client = OpenAI(
    api_key=os.getenv("OPENAI_API_KEY")
)

SYSTEM_PROMPT = """You are an expert SDET and SRE.

Classify failures into:
- CODE_BUG
- FLAKY_TEST
- ENV_ISSUE
- DATA_ISSUE
- INFRA_FAILURE

Rules:
- Use strong evidence
- No guessing
"""


def build_prompt(failures: List[Dict]) -> str:
    prompt = "Analyze the following test failures:\n\n"

    for i, f in enumerate(failures):
        prompt += f"""
Test {i+1}
Name: {f['test_name']}

Error:
{f['error'][:500]}

Logs:
{f.get('logs','')[:1500]}
"""

    prompt += """
Return JSON inside <json> tags:

<json>
[
  {
    "test_name": "...",
    "classification": "...",
    "reason": "...",
    "fix": "..."
  }
]
</json>
"""
    return prompt


def classify_failures_batch(failures: List[Dict], retries=3):

    if not failures:
        return []

    prompt = build_prompt(failures)

    for attempt in range(retries):
        try:
            logger.info(f"OpenAI call attempt {attempt+1}")

            # Replaced Anthropic with OpenAI
            response = client.chat.completions.create(
                model="gpt-4o-mini",   # fast + cheap + stable
                temperature=0.2,
                messages=[
                    {"role": "system", "content": SYSTEM_PROMPT},
                    {"role": "user", "content": prompt}
                ]
            )

            raw = response.choices[0].message.content

            return extract_json(raw)

        except Exception as e:
            logger.error(f"Attempt {attempt+1} failed: {e}")
            time.sleep(2 ** attempt)

    return fallback(failures)


def extract_json(text: str):

    match = re.search(r"<json>(.*?)</json>", text, re.DOTALL)
    if match:
        try:
            return json.loads(match.group(1).strip())
        except:
            pass

    match = re.search(r"\[[\s\S]+\]", text)
    if match:
        try:
            return json.loads(match.group(0))
        except:
            pass

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
