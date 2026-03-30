# import os
# import time
# import json
# import re
# # import anthropic  
# import logging
# from typing import List, Dict

# from openai import OpenAI   # Added

# logger = logging.getLogger(__name__)

# # client = anthropic.Anthropic(
# #     api_key=os.getenv("ANTHROPIC_API_KEY")
# # )

# # penAI client
# client = OpenAI(
#     api_key=os.getenv("OPENAI_API_KEY")
# )

# SYSTEM_PROMPT = """You are an expert SDET and SRE.

# Classify failures into:
# - CODE_BUG
# - FLAKY_TEST
# - ENV_ISSUE
# - DATA_ISSUE
# - INFRA_FAILURE

# Rules:
# - Use strong evidence
# - No guessing
# """


# def build_prompt(failures: List[Dict]) -> str:
#     prompt = "Analyze the following test failures:\n\n"

#     for i, f in enumerate(failures):
#         prompt += f"""
# Test {i+1}
# Name: {f['test_name']}

# Error:
# {f['error'][:500]}

# Logs:
# {f.get('logs','')[:500]}
# """

#     prompt += """
# Return JSON inside <json> tags:

# <json>
# [
#   {
#     "test_name": "...",
#     "classification": "...",
#     "reason": "...",
#     "fix": "..."
#   }
# ]
# </json>
# """
#     return prompt


# def classify_failures_batch(failures: List[Dict], retries=3):

#     if not failures:
#         return []

#     prompt = build_prompt(failures)

#     for attempt in range(retries):
#         try:
#             logger.info(f"OpenAI call attempt {attempt+1}")

#             # Replaced Anthropic with OpenAI
#             response = client.chat.completions.create(
#                 model="gpt-4.1-mini",   # fast + cheap + stable
#                 temperature=0.2,
#                 messages=[
#                     {"role": "system", "content": SYSTEM_PROMPT},
#                     {"role": "user", "content": prompt}
#                 ]
#             )

#             raw = response.choices[0].message.content

#             return extract_json(raw)

#         except Exception as e:
#             logger.error(f"Attempt {attempt+1} failed: {e}")
#             time.sleep(2 ** attempt)

#     return fallback(failures)


# def extract_json(text: str):

#     match = re.search(r"<json>(.*?)</json>", text, re.DOTALL)
#     if match:
#         try:
#             return json.loads(match.group(1).strip())
#         except:
#             pass

#     match = re.search(r"\[[\s\S]+\]", text)
#     if match:
#         try:
#             return json.loads(match.group(0))
#         except:
#             pass

#     raise ValueError("Invalid JSON from model")


# def fallback(failures: List[Dict]):

#     return [
#         {
#             "test_name": f["test_name"],
#             "classification": "UNKNOWN",
#             "reason": "AI failed",
#             "fix": "Manual analysis required"
#         }
#         for f in failures
#     ]


# shorter prompt
# import os
# import time
# import json
# import re
# import logging
# from typing import List, Dict

# from openai import OpenAI

# logger = logging.getLogger(__name__)

# # OpenAI client
# client = OpenAI(
#     api_key=os.getenv("OPENAI_API_KEY")
# )

# SYSTEM_PROMPT = """You are an expert test failure analyzer.

# Give only the failure reason in one short line.
# Be precise. No long explanations.
# """


# def build_prompt(failures: List[Dict]) -> str:
#     prompt = "Give short failure reason for each test:\n\n"

#     for i, f in enumerate(failures):
#         prompt += f"""
# Test {i+1}
# Name: {f['test_name']}

# Error:
# {f['error'][:200]}
# """

#     prompt += """
# Return JSON:

# [
#   {
#     "test_name": "...",
#     "reason": "short reason"
#   }
# ]
# """

#     return prompt


# def classify_failures_batch(failures: List[Dict], retries=3):

#     if not failures:
#         return []

#     # Optional safety (recommended for CI stability)
#     failures = failures[:5]

#     prompt = build_prompt(failures)

#     for attempt in range(retries):
#         try:
#             logger.info(f"OpenAI call attempt {attempt+1}")
#             logger.info(f"Prompt size: {len(prompt)} chars")

#             response = client.chat.completions.create(
#                 model="gpt-4.1-mini",
#                 temperature=0.2,
#                 messages=[
#                     {"role": "system", "content": SYSTEM_PROMPT},
#                     {"role": "user", "content": prompt}
#                 ]
#             )

#             raw = response.choices[0].message.content

#             return extract_json(raw)

#         except Exception as e:
#             logger.error(f"Attempt {attempt+1} failed: {e}")
#             time.sleep(2 ** attempt)

#     return fallback(failures)


# def extract_json(text: str):

#     match = re.search(r"\[[\s\S]+\]", text)
#     if match:
#         try:
#             return json.loads(match.group(0))
#         except Exception as e:
#             logger.error(f"JSON parsing failed: {e}")

#     raise ValueError("Invalid JSON from model")


# def fallback(failures: List[Dict]):

#     return [
#         {
#             "test_name": f["test_name"],
#             "reason": "AI failed"
#         }
#         for f in failures
#     ]



# google gemini
# import os
# import time
# import json
# import re
# import logging
# from typing import List, Dict

# from google import genai

# logger = logging.getLogger(__name__)

# # Gemini client (new SDK)
# client = genai.Client(
#     api_key=os.getenv("GEMINI_API_KEY")
# )

# MODEL = "gemini-2.5-flash-lite"  # ✅ free + working

# SYSTEM_PROMPT = """You are an expert test failure analyzer.

# Give only the failure reason in one short line.
# Be precise. No long explanations.
# Return ONLY valid JSON.
# """


# def build_prompt(failures: List[Dict]) -> str:
#     prompt = SYSTEM_PROMPT + "\n\n"
#     prompt += "Give short failure reason for each test:\n\n"

#     for i, f in enumerate(failures):
#         prompt += f"""
# Test {i+1}
# Name: {f['test_name']}

# Error:
# {f['error'][:200]}
# """

#     prompt += """
# Return JSON:

# [
#   {
#     "test_name": "...",
#     "reason": "short reason"
#   }
# ]
# """

#     return prompt


# def classify_failures_batch(failures: List[Dict], retries=3):

#     if not failures:
#         return []

#     failures = failures[:5]  # keep small for stability

#     prompt = build_prompt(failures)

#     for attempt in range(retries):
#         try:
#             logger.info(f"Gemini call attempt {attempt+1}")
#             logger.info(f"Prompt size: {len(prompt)} chars")

#             response = client.models.generate_content(
#                 model=MODEL,
#                 contents=prompt
#             )

#             raw = response.text

#             return extract_json(raw)

#         except Exception as e:
#             logger.error(f"Attempt {attempt+1} failed: {e}")
#             time.sleep(2 ** attempt)

#     return fallback(failures)


# def extract_json(text: str):

#     match = re.search(r"\[[\s\S]+\]", text)
#     if match:
#         try:
#             return json.loads(match.group(0))
#         except Exception as e:
#             logger.error(f"JSON parsing failed: {e}")

#     raise ValueError("Invalid JSON from model")


# def fallback(failures: List[Dict]):

#     return [
#         {
#             "test_name": f["test_name"],
#             "reason": "AI failed"
#         }
#         for f in failures
#     ]


# original prompt
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
MODEL = "gemini-2.5-flash"

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

#
