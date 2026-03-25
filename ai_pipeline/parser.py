import xml.etree.ElementTree as ET
import os

def parse_test_results():

    results = []

    # Get dynamic path from GitHub Action
    base_path = os.getenv("RESULTS_PATH", "test-results")

    report_folder = os.path.join(base_path, "surefire-reports")
    screenshot_folder = os.path.join(base_path, "screenshots")
    log_folder = os.path.join(base_path, "logs")

    # Safety check
    if not os.path.exists(report_folder):
        print(f"Report folder not found: {report_folder}")
        return results

    for file in os.listdir(report_folder):

        if not file.endswith(".xml"):
            continue

        file_path = os.path.join(report_folder, file)

        try:
            tree = ET.parse(file_path)
            root = tree.getroot()
        except Exception as e:
            print(f"Failed to parse XML: {file} | Error: {e}")
            continue

        for testcase in root.findall(".//testcase"):

            test_name = testcase.get("name")
            class_name = testcase.get("classname")

            status = "PASS"
            error_message = ""

            # FAILURE
            failure = testcase.find("failure")
            if failure is not None:
                status = "FAIL"
                error_message = failure.text.strip() if failure.text else ""

            # SKIPPED
            skipped = testcase.find("skipped")
            if skipped is not None:
                status = "SKIPPED"
                error_message = skipped.text.strip() if skipped.text else ""

            # Skip PASS tests (optional optimization)
            if status == "PASS":
                continue

            # Screenshot mapping
            screenshot_path = None
            if os.path.exists(screenshot_folder):
                for f in os.listdir(screenshot_folder):
                    if test_name in f:
                        screenshot_path = os.path.join(screenshot_folder, f)
                        break

            # Logs mapping
            log_content = ""
            log_file = os.path.join(log_folder, f"{test_name}.log")

            if os.path.exists(log_file):
                try:
                    with open(log_file, "r", encoding="utf-8") as lf:
                        log_content = lf.read()
                except Exception as e:
                    print(f"Failed to read log for {test_name}: {e}")

            #  Final structured output
            results.append({
                "test_name": test_name,
                "class": class_name,
                "status": status,
                "error": error_message,
                "logs": log_content,
                "screenshot": screenshot_path
            })

    print(f"Parsed {len(results)} failed/skipped tests")
    return results
