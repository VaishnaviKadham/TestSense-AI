import smtplib
from email.message import EmailMessage
import os


def send_email(ai_report, flakiness_report):

    msg = EmailMessage()
    msg['Subject'] = '🚀 AI QA Reports'
    msg['From'] = os.getenv("EMAIL_USER")
    msg['To'] = os.getenv("EMAIL_TO")

    msg.set_content("AI Reports Attached")

    with open(ai_report, "rb") as f:
        msg.add_attachment(
            f.read(),
            maintype="text",
            subtype="html",
            filename="ai_report.html"
        )

    with open(flakiness_report, "rb") as f:
        msg.add_attachment(
            f.read(),
            maintype="text",
            subtype="html",
            filename="flakiness_report.html"
        )

    with smtplib.SMTP('smtp.gmail.com', 587) as s:
        s.starttls()
        s.login(os.getenv("EMAIL_USER"), os.getenv("EMAIL_PASS"))
        s.send_message(msg)
