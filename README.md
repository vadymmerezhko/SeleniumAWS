## SeleniumAWS
### Command line parameters
TestNG file name:
```bash
-DtestngFile=<file name>
```
Test suite path:
```bash
-DtestSuite=<test suite path>
```
Thread count:
```bash
-DthreadCount=<trtead count>
```
Test mode:
```bash
-DtestMode=<test mode>
```
Test modes:
- Local:
```bash
-DtestMode=local
```
- Local Docker:
```bash
-DtestMode=local_docker
```
- Local Playwright:
```bash
-DtestMode=local_playwright
```
- Local Playwright accessibility:
```bash
-DtestMode=local_accessibility
```
- AWS Docker:
```bash
-DtestMode=aws_docker
```
- AWS Device farm:
```bash
-DtestMode=aws_device_farm
```
- Remote (Selenium Grid):
```bash
-DtestMode=remote
```
- Local Appium:
```bash
-DtestMode=local_appium";
```
- AWS Lambda:
```bash
-DtestMode=aws_lambda
```
- AWS RMI:
```bash
-DtestMode=aws_rmi
```
Browser name:
```bash
-Dbrowser=<browser name>
```
Browser name and version:
```bash
-Dbrowser=<browser name>:<browser version>
```
Browser names:
- Local mode Chrome stable:
```bash
-Dbrowser=chrome:stable
```
- Local mode Chrome beta:
```bash
-Dbrowser=chrome:beta
```
- Local mode Chrome canary:
```bash
-Dbrowser=chrome:canary
```
- Local Docker mode Chrome latest:
```bash
-Dbrowser=chrome:latest
```
- Local Docker mode Chrome 112:
```bash
-Dbrowser=chrome:112.0
```
- Local mode Firefox stable:
```bash
-Dbrowser=firefox:stable
```
- Local Docker mode Firefox latest:
```bash
-Dbrowser=chrome:latest
```
- Local mode Edge stable:
```bash
-Dbrowser=firefox:stable
```
- Local Playwright mode Firefox:
```bash
-Dbrowser=firefox
```
- Local Playwright mode Chromium:
```bash
-Dbrowser=firefox:chromium
```
- Local Playwright mode WebKit (Safari):
```bash
-Dbrowser=firefox:webkit
```
- Headless:
```bash
-Dheadless=true
```
- Non-headless:
```bash
-Dheadless=false
```
- Remote host:
```bash
-DremoteHost=<remote host URL>
```
- Remote host example:
```bash
-DremoteHost=http://localhost:4444
```
### Test Run Command Lines
#### Local Run - Chrome stable
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local" "-Dbrowser=firefox:stable"
```      
#### AWS Local Docker - Chrome latest
TODO: Disabled till Maven exec plugin vulnerability is fixed.
```bash
mvn -f aws-local-pom.xml clean compile exec:java "-DthreadCount=4" "-DtestMode=local_docker" "-Dbrowser=chrome:latest" "-DtestngFile=testngMethod.xml"
```      
#### Local Docker - Linux Firefox
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_docker" "-Dbrowser=firefox:latest"
```
#### Local WebDriver - Linux Chrome
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local" "-Dbrowser=chrome:stable"
```
#### Local Playwright - Linux Firefox
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_playwright" "-Dbrowser=firefox"
```
#### AWS Device Farm - Windows Edge
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=5" "-DtestMode=aws_device_farm" "-Dbrowser=edge:latest"
```
#### AWS Remote Docker - Linux Chrome
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=8" "-DtestMode=aws_docker" "-Dbrowser=chrome:latest"
```
#### AWS Lambda - Playwright Firefox
TODO: Disabled till Maven exec plugin vulnerability is fixed.
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=1" "-DtestMode=aws_lambda"
```
#### AWS RMI Docker - Chrome latest
TODO: Disabled till Maven exec plugin vulnerability is fixed.
```bash
mvn clean test "-DtestSuite=./src/test/resources/testng1.xml" "-DthreadCount=4" "-DtestMode=aws_rmi" "-Dbrowser=chrome:latest"
```
