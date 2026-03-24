import xml.etree.ElementTree as ET
import os

def parse_test_results():

    results = []
    report_folder = "target/surefire-reports"
    screenshot_folder = "screenshots"
    log_folder = "logs"

    for file in os.listdir(report_folder):

        if file.endswith(".xml"):

            tree = ET.parse(os.path.join(report_folder, file))
            root = tree.getroot()

            for testcase in root.findall(".//testcase"):

                test_name = testcase.get("name")
                class_name = testcase.get("classname")

                status = "PASS"
                error_message = ""

                if testcase.find("failure") is not None:
                    status = "FAIL"
                    error_message = testcase.find("failure").text

                elif testcase.find("skipped") is not None:
                    status = "SKIPPED"
                    error_message = testcase.find("skipped").text

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
                    with open(log_file, "r") as lf:
                        log_content = lf.read()

                results.append({
                    "test_name": test_name,
                    "class": class_name,
                    "status": status,
                    "error": error_message,
                    "logs": log_content,
                    "screenshot": screenshot_path
                })

    return results
