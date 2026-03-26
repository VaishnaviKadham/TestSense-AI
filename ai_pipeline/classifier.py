
from openai import OpenAI
import os

# NEW CLIENT (v1+ API)
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

def classify_failure(error, logs):

    prompt = f"""
    You are an expert SDET (Senior Software Development Engineer in Test) 
and SRE with 15+ years of experience analyzing test failures.

Classify the root cause into ONE of:
- CODE_BUG
- FLAKY_TEST
- ENV_ISSUE
- DATA_ISSUE
- INFRA_FAILURE

Rules:
- Use evidence from error + logs
- Be precise, no guessing

Error:
{error}

Logs:
{logs}

Return strictly in format:
Classification: <type>
Reason: <reason>
Fix: <fix>
"""

    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "user", "content": prompt}
            ],
            temperature=0.2
        )

        return response.choices[0].message.content.strip()

    except Exception as e:
        print(f"LLM Error: {e}")
        return "Classification: UNKNOWN\nReason: LLM failed\nFix: Retry"
