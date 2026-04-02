# TestSense-AI
AI Test Failure Analyzer

Test Sense AI is an intelligent failure analysis system designed to enhance QA workflows by automatically analyzing CI/CD test failures. It leverages AI/LLMs to classify failures, suggest fixes, track flakiness, and generate actionable reports.

# Architecture
## System Architecture

### 🔄 Workflow Overview

1. **CI/CD Pipeline**
   - GitHub Actions / Jenkins

2. **Test Execution**
   - Selenium + TestNG

3. **Artifacts Generated**
   - Logs  
   - Surefire XML  
   - Extent Reports  
   - Screenshots  

4. **AI Failure Analysis Job (Auto Triggered)**
   - Parse failures  
   - Fetch logs & screenshots  
   - LLM-based classification  
   - Suggest fixes  
   - Create Jira tickets (if bug)  
   - Track flakiness  
   - Generate report  

5. **Notifications**
   - Email  
   - Slack  
   - Microsoft Teams
  
   
