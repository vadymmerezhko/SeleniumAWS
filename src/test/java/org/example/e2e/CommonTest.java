package org.example.e2e;

import org.example.asserts.SmartAssert;
import org.example.data.*;
import org.example.configs.TestConfig;
import org.example.drivers.factories.WebDriverFactory;
import org.example.servers.TestServerInterface;
import org.example.servers.TestServerManager;
import org.example.tests.BaseTest;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;

public class CommonTest extends BaseTest {
    static private final TestConfig config = TestConfig.getInstance();

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.quiteAllBrowsersAndServers();
    }

    protected void fillWebForm() {
        WebFormPageInput webFormPageInput = new WebFormPageInput();
       fillWebForm(webFormPageInput);
    }

    protected void failFillWebForm() {
        fillWebForm();
        if (config.getDebugFail()) {
            Assert.fail("Test is failed for debug purpose.");
        }
    }

    protected void submitWebForm() {
        TestServerInterface testServer = TestServerManager.getTestServer();
        TargetPageOutput targetPageOutput = testServer.submitWebForm();
        TargetPageOutput expectedOutput = new TargetPageOutput();

        Assert.assertEquals(targetPageOutput.getHeader(), expectedOutput.getHeader());
        Assert.assertEquals(targetPageOutput.getStatus(), expectedOutput.getStatus());
    }

    private void fillWebForm(WebFormPageInput input) {
        Reporter.log("<b>fillWebForm test execution started.</b>");

        TestServerInterface testServer = TestServerManager.getTestServer();
        WebFormPageOutput output = testServer.fillWebForm(input);
        WebFormPageOutput expected = new WebFormPageOutput();

        SmartAssert.assertDataObjects(expected, output);

/*        Assert.assertEquals(output.getTextInput(), expected.getTextInput(), "Text input.");
        Assert.assertEquals(output.getTextareaInput(), expected.getTextareaInput(), "Textarea input.");
        Assert.assertEquals(output.getDropdownSelectedOption(), expected.getDropdownSelectedOption(), "Dropdown.");
        Assert.assertEquals(output.getDataListSelectOption(), expected.getDataListSelectOption(), "Data list.");
        // TODO: Fix file path for remote run.
        //Assert.assertTrue((testResult.filePath().contains("pom.xml")));
        //TODO: fix checkbox value for Android
        Assert.assertEquals(output.getCheckbox1Value(), expected.getCheckbox1Value(), "Checkbox 1.");
        Assert.assertEquals(output.getRadiobutton1Value(), expected.getRadiobutton1Value(), "Checkbox 2.");
        Assert.assertEquals(output.getRadiobutton2Value(), expected.getRadiobutton2Value());
        Assert.assertEquals(output.getColor(), expected.getColor(), "Color.");

        String outputDate = output.getDate();
        String expectedDate = expected.getDate();

        Assert.assertEquals(output.getDate(), expected.getDate(), "Date.");
        Assert.assertEquals(output.getRange(), expected.getRange(), "Range.");*/

        Reporter.log("<b>fillWebForm test execution finished.</b>");
    }
}
