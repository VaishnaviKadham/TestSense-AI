import os
import time
import json
import re
import anthropic
import logging
from typing import List, Dict

logger = logging.getLogger(__name__)

client = anthropic.Anthropic(
    api_key=os.getenv("ANTHROPIC_API_KEY"),
    timeout=30
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
- Be precise
- No guessing
"""

# -----------------------------
# BUILD PROMPT (BATCH)
# -----------------------------
def build_prompt(failures: List[Dict]) -> str:
    prompt = "Analyze the following test failures:\n\n"

    for i, f in enumerate(failures):
        prompt += f"""
Test {i+1}:
Name: {f['test_name']}

Error:
{f['error'][:500]}

Logs:
{f.get('logs','')[:1500]}
"""

    prompt += """
Return JSON list inside <json> tags:

<json>
[
  {
    "test_name": "...",
    "classification": "CODE_BUG|FLAKY_TEST|ENV_ISSUE|DATA_ISSUE|INFRA_FAILURE",
    "reason": "...",
    "fix": "..."
  }
]
</json>
"""
    return prompt


# -----------------------------
# MAIN FUNCTION
# -----------------------------
def classify_failures_batch(failures: List[Dict], retries=3) -> List[Dict]:

    if not failures:
        return []

    prompt = build_prompt(failures)

    for attempt in range(retries):
        try:
            logger.info(f"Calling Claude (attempt {attempt+1})")

            response = client.messages.create(
                model="claude-3-5-sonnet-20241022",
                max_tokens=1200,
                temperature=0.2,
                system=SYSTEM_PROMPT,
                messages=[{"role": "user", "content": prompt}]
            )

            raw = response.content[0].text

            return extract_json(raw)

        except Exception as e:
            logger.error(f"Attempt {attempt+1} failed: {e}")
            time.sleep(2 ** attempt)

    return fallback(failures)


# -----------------------------
# JSON PARSER
# -----------------------------
def extract_json(text: str) -> List[Dict]:

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

    raise ValueError("Invalid JSON from LLM")


# -----------------------------
# FALLBACK
# -----------------------------
def fallback(failures: List[Dict]) -> List[Dict]:

    results = []

    for f in failures:
        results.append({
            "test_name": f["test_name"],
            "classification": "UNKNOWN",
            "reason": "AI failed",
            "fix": "Check logs manually"
        })

    return results
