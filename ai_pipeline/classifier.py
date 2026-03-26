"""
TestSense AI — AI Analysis Engine (Optimized & Stable)
"""

import json
import re
import logging
import time
from typing import List, Dict

import anthropic
from core.config import settings

logger = logging.getLogger("testsense.ai_engine")

client = anthropic.Anthropic(
    api_key=settings.ANTHROPIC_API_KEY,
    timeout=30  # prevents hanging
)

# -------------------------------
# SYSTEM PROMPT
# -------------------------------
SYSTEM_PROMPT = """You are an expert SDET and SRE with 15+ years experience.

Analyze test failures and classify root cause into:
- CODE_BUG
- FLAKY_TEST
- ENV_ISSUE
- DATA_ISSUE
- INFRA_FAILURE

Rules:
- NEVER guess
- Use evidence from logs
- Be precise

Always think step-by-step in <thinking> tags.
Return final answer ONLY inside <json> tags.
"""

# -------------------------------
# PROMPT BUILDER (BATCH MODE)
# -------------------------------
def build_batch_prompt(failures: List[Dict]) -> str:
    prompt = "## Batch Test Failure Analysis\n\n"

    for i, f in enumerate(failures):
        prompt += f"""
### Test {i+1}
Name: {f.get('test_name')}

Error:
{f.get('error_message', '')[:500]}

Stack Trace:
{f.get('stack_trace', '')[:1500]}
"""

    prompt += """
---
Analyze ALL failures.

Return JSON list:

<json>
[
  {
    "test_name": "...",
    "classification": "...",
    "confidence_score": 85,
    "root_cause_summary": "...",
    "suggested_fix": "...",
    "should_create_jira": true
  }
]
</json>
"""
    return prompt


# -------------------------------
# MAIN ANALYSIS FUNCTION
# -------------------------------
def analyze_failures_batch(failures: List[Dict], retries=3) -> List[Dict]:

    prompt = build_batch_prompt(failures)

    for attempt in range(retries):
        try:
            logger.info(f"Calling Claude (attempt {attempt+1})...")

            response = client.messages.create(
                model="claude-3-5-sonnet-20241022",  # stable & powerful
                max_tokens=1500,
                temperature=0.2,
                system=SYSTEM_PROMPT,
                messages=[{"role": "user", "content": prompt}]
            )

            raw_text = response.content[0].text

            result = _extract_json(raw_text)

            logger.info("Batch analysis successful")
            return result

        except Exception as e:
            logger.error(f"Attempt {attempt+1} failed: {e}")
            time.sleep(2 ** attempt)

    logger.error("All retries failed → using fallback")
    return _fallback_batch(failures)


# -------------------------------
# JSON EXTRACTION
# -------------------------------
def _extract_json(raw_text: str):

    # Try <json> block first
    match = re.search(r"<json>(.*?)</json>", raw_text, re.DOTALL)
    if match:
        try:
            return json.loads(match.group(1).strip())
        except Exception:
            pass

    # fallback: find JSON array
    match = re.search(r"\[[\s\S]+\]", raw_text)
    if match:
        try:
            return json.loads(match.group(0))
        except Exception:
            pass

    raise ValueError("Invalid JSON from LLM")


# -------------------------------
# FALLBACK
# -------------------------------
def _fallback_batch(failures: List[Dict]) -> List[Dict]:
    results = []

    for f in failures:
        results.append({
            "test_name": f.get("test_name"),
            "classification": "UNKNOWN",
            "confidence_score": 0,
            "root_cause_summary": "AI failed — manual review required",
            "suggested_fix": "Check logs manually",
            "should_create_jira": False
        })

    return results
