import openai
import os

openai.api_key = os.getenv("OPENAI_API_KEY")

def classify_failure(error, logs):

    prompt = """You are an expert SDET (Senior Software Development Engineer in Test) 
and SRE with 15+ years of experience analyzing test failures across large-scale 
distributed systems at companies like Google, Amazon, and Microsoft.

Your job is to analyze test failure data and classify the root cause.

Classification types you must choose from:
- CODE_BUG: Production code has a genuine defect. The test is correct, the code is wrong.
- FLAKY_TEST: The test itself is unstable due to timing, race conditions, test ordering, 
  or external dependencies. The code may be fine.
- ENV_ISSUE: Failure is caused by the environment — wrong database, misconfigured service, 
  wrong environment variables, or dependency version mismatch.
- DATA_ISSUE: Test data is missing, expired, corrupted, or the database is in an unexpected state.
- INFRA_FAILURE: The CI infrastructure itself failed — out of memory, disk full, network timeout, 
  Docker issues, or runner crash. Not caused by the application code.

Rules for reasoning:
1. NEVER guess — base your classification on specific evidence in the stack trace and context.
2. A TimeoutException alone is NOT enough to classify as INFRA_FAILURE — timeouts also occur 
   in FLAKY_TEST (race conditions) and ENV_ISSUE (slow services).
3. If the failure is 100% consistent (always fails), lean toward CODE_BUG or ENV_ISSUE.
4. If the failure is intermittent (sometimes passes), lean toward FLAKY_TEST.
5. Look for keywords: "Connection refused" → ENV_ISSUE, "AssertionError" → CODE_BUG, 
   "OutOfMemory" → INFRA_FAILURE, "No such element" → FLAKY_TEST (in UI tests).

Always reason step-by-step in <thinking> tags before writing your final JSON answer.
Also provide a short root cause and fix.

Error:
{error}

Logs:
{logs}

Return in format:
Classification: <type>
Reason: <reason>
Fix: <fix>"""


    response = openai.ChatCompletion.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}]
    )

    return response["choices"][0]["message"]["content"]
