## SmartE2E Codeless Test Framework 1.7.40
### Command line parameters
#### -DsiteHost
AUT site host to switch between DEV, QA and PROD environments:
```bash
-DsiteHost=https://www.selenium.dev
```
#### -DtestSuite
Test suite path:
```bash
-DtestSuite=./src/test/resources/testng.xml
```
#### -DthreadCount
Parallel threads count (from 1 to 32, 4 is default):
```bash
-DthreadCount=4
```
#### -DtestMode
Test mode:
- local (default)
- local_auto
- local_appium
- local_docker
- local_docker_auto
- local_playwright
- local_accessibility
- remote
- aws_local (TODO)
- aws_docker
- aws_device_farm
```bash
-DtestMode=aws_docker
```
#### -DdebugMode
Runs tests in debug mode when user can see debug popup messages in case of test failure:
- true
- false (default)
#### -Dbrowser
Browser name:
- chrome
- chromium
- firefox
- edge
- safari
- webkit (local_playwright only)
```bash
-Dbrowser=firefox
```
Browser name and version.

For local, aws_local (TODO) test modes:
- chrome:stable (defalt)
- chrome:beta
- firefox:stable
- firefox:beta
- edge:stable
- edge:beta

For local_docker, local_docker_auto, aws_docker test modes:
- chrome:latest
- chrome:latest-1
- chrome:latest-{n}
- chrome:126.0
- chrome:{version}
- firefox:latest
- firefox:latest
- firefox:126.0
- edge:latest
- edge:latest-1
- edge:127.0
- safari:latest
- safari:latest-1
- safari:15.0

```bash
-Dbrowser=chrome:latest
```
#### -DbrowserSize
Browser size width:height:
 - browserSize=1052:630 (default)
```bash
-DbrowserSize=1052:630
```
#### -Dheadless
Headless:
- true (default)
- false
```bash
-Dheadless=false
```
#### -DremoteHost
Remote host http:/{host}:{port} : 
- http://127.0.0.1:4444
- http://localhost:4444
- http://192.168.1.1:4444
- http://test.server.com:4444
- https://your_username/your_access_key@ondemand.us-west-1.saucelabs.com/wd/hub
```bash
-DremoteHost=http://localhost:4444
```
#### -DretainBrowser
Retains browser session be open and resets cookies  after test ends to improve performance in 2 - 3 times:
- true (default)
- false (closes browser after every test)
#### DscreenshotOnFail
Takes screenshot on fail:
- true (default)
- false
```bash
-DscreenshotOnFail=true
```
#### DvideoOnFail
Records video on fail:
- true
- false (default)
```bash
-DvideoOnFail=false
```
#### -DdebugFail
Fail test for debug purpose:
- true
- false (default)
Only for (otherwise causes an exception):
- -DtestMode=local
- -Dheadless=false
- -DthreadCount=1
```bash
-DdebugFail=true -DtestMode=local -Dheadless=false -DthreadCount=1
```
#### -Dhighlight
Highlights current element for debug purpose:
- true
- false (default)
```bash
-Dhighlight=true
```
#### -DstepDelay
Step delay for debug purpose (milliseconds):
- 0 (default)
- 500
```bash
-DstepDelay=200
```
### Test modes
#### local
For already installed browsers and WebDrivers:
- chrome
- firefox
- edge
- safari
```bash
-DtestMode=local -Dbrowser=chrome:stable
```
#### local_auto
Downloads browser and WebDriver binaries before test for browsers:
- chrome:stable
- chrome:beta
- firefox:stable
- edge:stable
```bash
-DtestMode=local_auto -Dbrowser=chrome:latest
```
#### local_docker
For running Docker and browsers:
- chrome:latest
- chrome:latest-1
- chrome:126.0
- firefox:latest
- firefox:latest
- firefox:126.0
- edge:latest
- edge:latest-1
- edge:127.0
```bash
-DtestMode=local_docker
```
#### local_docker_auto
For running Docker and browsers:
- chrome:latest
- chrome:latest-1
- chrome:126.0
- firefox:latest
- firefox:latest
- firefox:126.0
- edge:latest
- edge:latest-1
- edge:127.0
- safari:latest
- safari:latest-1
- safari:15.0
```bash
-DtestMode=local_docker_auto -Dbrowser=safari:latest
```
#### local_playwright
For browsers:
- chromium
- firefox
- webkit
```bash
-DtestMode=local_playwright -Dbrowser=firefox
```
#### local_appium:
TODO
```bash
-DtestMode=local_appium
```
#### local_accessibility
For chromium browser only:
```bash
-DtestMode=local_accessibility
```
#### aws_docker
For browsers running on AWS EC2:
- chrome:latest
- chrome:latest-1
- chrome:126.0
- firefox:latest
- firefox:latest
- firefox:126.0
- edge:latest
- edge:latest-1
- edge:127.0
```bash
-DtestMode=aws_docker -Dbrowser:firefox:latest
```
#### aws_device_farm
For Windows browsers running on AWS Device Farm:
- chrome:latest
- chrome:latest-1
- firefox:latest
- firefox:latest
- edge:latest
- edge:latest-1
```bash
-DtestMode=aws_device_farm -Browser=edge:latests
```
#### aws_docker
TODO
#### remote
For Selenium Grid running locally or on server and browsers:
- chrome
- edge
- firefox
- safari
```bash
-DtestMode=remote -DremoteHost=http:\\192.168.1.1:4444 -Dbrowser:chrome
```
#### -DpageWaitTimeout
Wait for page load timeout seconds:
- 30 (default)
- 60
```bash
-DpageWaitTimeout=60
```
#### -DelementWaitTimeout
Wait for element timeout seconds:
- 8 (default)
- 30
```bash
-DelementWaitTimeout=10
```
#### -DretryWait
Test step retry on error wait delay milliseconds:
- 200 (default)
- 500
```bash
-DretryWait=100
```
#### -DretryTimeout
Test step retry on error wait timeout milliseconds:
- 8000 (default)
- 30000
```bash
-DretryTimeout=4000
```
#### -DpagesFolderPath
Folder path for storing page object JSON files:
- ./src/test/resources/pages
- ./any/folder/path
```bash
-DpagesFolderPath=./src/test/resources/pages
```
#### -DdataFolderPath
Folder path for storing data object JSON files:
- ./src/test/resources/data
- ./any/folder/path
```bash
-DdataFolderPath=./src/test/resources/data
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
#### AWS Docker - Chrome latest
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=8" "-DtestMode=aws_docker" "-Dbrowser=chrome:latest"
```
#### Local Docker - Firefox latest
Runs tests in debug mode. User can define and fix element selectors at runtime.
Use only headed mode and only one thread. 
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngMethod.xml" "-DthreadCount=1" "-DtestMode=local" "-Dbrowser=firefox:stable" "-Dheadless=false" "-DthreadCount=1" "-DdebugMode=true"
```
#### Unit Tests
```bash
mvn clean test "-DtestSuite=./src/test/resources/testngUnitTests.xml"
```
