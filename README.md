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
Thread count (from 1 to 32):
```bash
-DthreadCount=<trtead count>
```
Test mode (local, local_auto, local_appium, remote, local_docker, local_docker_auto, aws_docker, aws_device_farm, local_playwright, aws_lambda, aws_rmi, local_accessibility):
```bash
-DtestMode=<test mode>
```
Browser name (chrome, chromium, firefox, edge, safari, webkit):
```bash
-Dbrowser=<browser name>
```
Browser name and version (latest, latest-1, dev, stable, beta, canary):
```bash
-Dbrowser=<browser name>:<browser version>
```
Headless (true, false):
```bash
-Dheadless=true
```
Non-headless (true/false):
```bash
-Dheadless=false
```
Remote host (like http://127.0.0.1:4444):
```bash
-DremoteHost=<remote host URL>
```
Remote host example:
```bash
-DremoteHost=http://localhost:4444
```
### Test modes
Local (for chrome:stable, chrome:beta, chrome:canary, firefox:stable, edge:stable):
```bash
-DtestMode=local
```
Local Auto (for installed chrome, firefox, edge, safari):
```bash
-DtestMode=local_auto
```
Local Docker (for chrome:version, edge:version, firefox:version,
for versions: latest, latest-1, latest-n, 124.0, beta, dev):
```bash
-DtestMode=local_docker
```
Local Docker Auto (for chrome:version, chromium:version, edge:<version>, firefox:<version>, safari:version,
for versions: latest, latest-1, latest-n, 124.0, beta, dev):
```bash
-DtestMode=local_docker_auto
```
Local Playwright (for chromium, firefox, webkit):
```bash
-DtestMode=local_playwright
```
Local Playwright accessibility (for chromium only):
```bash
-DtestMode=local_accessibility
```
AWS Docker (for chrome:version, edge:version, firefox:version,
for versions: latest, latest-1, latest-n, 124.0, beta, dev):
```bash
-DtestMode=aws_docker
```
AWS Device farm (for chrome:version, edge:version, firefox:latest on Windows,
for versions: latest, latest-1, latest-n):
```bash
-DtestMode=aws_device_farm
```
Remote (Selenium Grid for chrome, edge, firefox, safari):
```bash
-DtestMode=remote
```
Local Appium:
```bash
-DtestMode=local_appium";
```
AWS Lambda (for Playwright firefox only):
```bash
-DtestMode=aws_lambda
```
AWS RMI:
```bash
-DtestMode=aws_rmi
```
#### Browser names:
Local mode Chrome stable:
```bash
-Dbrowser=chrome:stable
```
Local mode Chrome beta:
```bash
-Dbrowser=chrome:beta
```
Local mode Chrome canary:
```bash
-Dbrowser=chrome:canary
```
Local Docker mode Chrome latest:
```bash
-Dbrowser=chrome:latest
```
Local Docker mode Chrome latest - 1:
```bash
-Dbrowser=chrome:latest-1
```
Local Docker mode Chrome 112:
```bash
-Dbrowser=chrome:112.0
```
Local mode Firefox stable:
```bash
-Dbrowser=firefox:stable
```
Local Docker mode Firefox latest:
```bash
-Dbrowser=chrome:latest
```
Local Docker Auto mode Safari latest:
```bash
-Dbrowser=safari:latest
```
Local mode Edge stable:
```bash
-Dbrowser=firefox:stable
```
Local Playwright mode Firefox:
```bash
-Dbrowser=firefox
```
Local Playwright mode Chromium:
```bash
-Dbrowser=firefox:chromium
```
Local Playwright mode WebKit:
```bash
-Dbrowser=firefox:webkit
```
### Test Run Command Lines
#### Local Run - Chrome stable
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local" "-Dbrowser=firefox:stable"
```    
#### Local Run Auto - Firefox
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_auto" "-Dbrowser=firefox"
```    
#### AWS Local Docker - Chrome latest
TODO: Disabled till Maven exec plugin vulnerability is fixed.
```bash
mvn -f aws-local-pom.xml clean compile exec:java "-DthreadCount=4" "-DtestMode=local_docker" "-Dbrowser=chrome:latest" "-DtestngFile=testngMethod.xml"
```
#### Local Docker - Firefox latest
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_docker" "-Dbrowser=firefox:latest"
```
#### Local Docker Auto - Safari latest
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_docker_auto" "-Dbrowser=safari:latest"
```
#### AWS Remote Selenium Server - Firefox
```bash
mvn test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=1" "-DtestMode=remote" "-Dbrowser=firefox:latest" "-DremoteHost=http://127.0.0.1:4444"
```
#### Local Playwright - Firefox
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=2" "-DtestMode=local_playwright" "-Dbrowser=firefox"
```
#### AWS Device Farm - Windows Edge
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=5" "-DtestMode=aws_device_farm" "-Dbrowser=edge:latest"
```
#### AWS Remote Docker - Chrome latest
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
