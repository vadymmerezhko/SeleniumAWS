package org.example;

import org.example.driver.WebDriverFactory;
import org.example.server.TestServer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class BaseTest {

    private static final String TEST_NG_METHOD_FILE_TEMPLATE = """
            <!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
            <suite name="Suite" thread-count="12" parallel="methods">
                <test name="Test">
                    <classes>
                        <class name="%CLASS_NAME%">
                            <methods>
                                <include name="%METHOD_NAME%" />
                            </methods>
                        </class>
                    </classes>
                </test> <!-- Test -->
            </suite> <!-- Suite -->
            """;
    private static final String CLASS_NAME_PLACEHOLDER = "%CLASS_NAME%";
    private static final String METHOD_NAME_PLACEHOLDER = "%METHOD_NAME%";

    protected WebDriver driver;

    protected void signUp(String className, String methodName) {
        String lambda = System.getProperty("lambda");

        if (lambda != null && lambda.toLowerCase().trim().equals("yes")) {
            System.out.println("Lambda for " + className + "." + methodName);
            String output = runLambdaFunction(className, methodName);

            System.out.println("Output:\n" + output);

            if (!output.contains("BUILD SUCCESS")) {
                Assert.fail(output);
            }
        }
        else {
            driver = WebDriverFactory.getDriver();
            String inputValue = "Selenium";
            TestServer testServer = new TestServer(driver);
            String actualValue = testServer.signUp(inputValue);
            Assert.assertEquals(actualValue, inputValue);
        }
    }

    private String runLambdaFunction(String className, String methodName) {
        String projectFolderPath = String.format("%s/%s", Settings.PROJECT_FOLDER_PATH, Settings.PROJECT_NAME);
        String fileFolderPath = String.format("%s/src/test/resources", projectFolderPath);
        String fileName = String.format("%s.%s.xml", className, methodName);
        String fileContent = TEST_NG_METHOD_FILE_TEMPLATE;
        fileContent = fileContent.replace(CLASS_NAME_PLACEHOLDER, className);
        fileContent = fileContent.replace(METHOD_NAME_PLACEHOLDER, methodName);

        FileManager.createFile(fileFolderPath, fileName, fileContent);

        String command = String.format(
                "mvn test \"-DtestSuite=%s/%s\" \"-DthreadCount=1\" \"-Dlambda=no\"\n",
                fileFolderPath, fileName);

        CommandLineExecutor.runCommandLine(String.format("cd \"%s\"", projectFolderPath));
        return CommandLineExecutor.runCommandLine(command);
    }
}
