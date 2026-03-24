import smtplib
from email.message import EmailMessage
import os

def send_email(file_path):

    msg = EmailMessage()
    msg['Subject'] = 'AI Test Report'
    msg['From'] = os.getenv("EMAIL_USER")
    msg['To'] = os.getenv("EMAIL_TO")

    with open(file_path, 'r') as f:
        msg.set_content(f.read(), subtype='html')

    with smtplib.SMTP('smtp.gmail.com', 587) as s:
        s.starttls()
        s.login(os.getenv("EMAIL_USER"), os.getenv("EMAIL_PASS"))
        s.send_message(msg)
