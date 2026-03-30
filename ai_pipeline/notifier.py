import smtplib
from email.message import EmailMessage
import os


# def send_email(file_path):

#     msg = EmailMessage()
#     msg['Subject'] = '🚀 AI Flakiness Report'
#     msg['From'] = os.getenv("EMAIL_USER")
#     msg['To'] = os.getenv("EMAIL_TO")

#     with open(file_path, 'r') as f:
#         html = f.read()

#     msg.add_alternative(html, subtype='html')

#     with smtplib.SMTP('smtp.gmail.com', 587) as s:
#         s.starttls()
#         s.login(os.getenv("EMAIL_USER"), os.getenv("EMAIL_PASS"))
#         s.send_message(msg)


def send_email(file_path):

    msg = EmailMessage()
    msg['Subject'] = '🚀 Flakiness Report Ready'
    msg['From'] = os.getenv("EMAIL_USER")
    msg['To'] = os.getenv("EMAIL_TO")

    msg.set_content(f"""
Flakiness report generated.

Download from GitHub Artifacts:
https://github.com/<your-repo>/actions

Or open index.html inside:
flakiness-report/
""")

    with smtplib.SMTP('smtp.gmail.com', 587) as s:
        s.starttls()
        s.login(os.getenv("EMAIL_USER"), os.getenv("EMAIL_PASS"))
        s.send_message(msg)
