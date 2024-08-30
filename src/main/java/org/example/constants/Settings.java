package org.example.constants;

import com.amazonaws.regions.Regions;
import org.example.configs.Config;

import java.awt.*;

/**
 * The setting constants.
 */
public class Settings {
    public static final Regions AWS_REGION = Regions.US_WEST_1;
    public static final int SELENIUM_SERVERS_COUNT = 1;
    public static int TEST_RETRY_LIMIT = 3;
    public static final int AWS_URL_EXPIRES_SECONDS = 60 * 15;
    public static final int REMOTE_WEB_DRIVER_PORT = 4444;
    public static final int SERVER_WAIT_TIMEOUT_SECONDS = 120;
    public static final int MIN_COLOURS_THRESHOLD_PERCENTAGE = 70;
    public static final int MIN_PIXELS_THRESHOLD_PERCENTAGE = 70;
    public static final int MAX_SIZE_THRESHOLD_PIXELS = 3;
    public static final int MAX_SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int MAX_SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    public static final int RETRY_WAIT_MILLISECONDS = Config.getInstance().getRetryWaitMSec();
    public static final int RETRY_TIMEOUT_MILLISECONDS = Config.getInstance().getRetryTimeoutSec();
    public static final int WAIT_ELEMENT_CHANGING_MILLISECONDS = 20;
    public static final int WAIT_ELEMENT_DELAY_MILLISECONDS = Config.getInstance().getElementWaitDelay();
    public static final int WAIT_ELEMENT_TIMEOUT_SECONDS = Config.getInstance().getElementWaitTimeout();
    public static final int PAGE_LOAD_WAIT_TIMEOUT_SECONDS = Config.getInstance().getPageWaitTimeout();
    public static final int RETRY_COUNT = 20;
    public static final int JSON_LAYOUT_SPACES = 4;

    public static final String AWS_DOCKER_IMAGE_ID = "ami-0d93c31a4c91fcd51";
    public static final String AWS_LOCAL_RUN_IMAGE_ID = "ami-05188da1ca88643c3";

    public static final String AWS_EC2_USER_DATA_TEMPLATE =
            "#!/bin/bash\n" +
            "sudo docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 " +
            "--shm-size=\"2g\" selenium/standalone-%s:%s";
    public static final String SECURITY_KEY_PAIR_NAME = "SeleniumKeyPair";
    public static final String SECURITY_GROUP_NAME = "Selenium Test Security Group";
    public static final String PAGE_URL_FIELD_NAME = "PAGE_URL";
    public static final String SITE_HOST_PLACEHOLDER = "#SITE_HOST#";
    public static final String PAGE_OBJECTS_FOLDER_PATH = Config.getInstance().getPagesFolderPath();
    public static final String DATA_OBJECTS_FOLDER_PATH = Config.getInstance().getDataFolderPath();
    public static final String IMAGE_FOLDER_PATH = Config.getInstance().getImagesFolderPath();
    public static final String TEST_WEBSITE_URL = "https://www.selenium.dev/selenium/web/web-form.html";
    public static final String TEST_CONFIG_FILE_PATH = "test-config.properties";
    public static final String KEYWORD_PLACEHOLDER = "#KEYWORD#";
    public static final String SELECTOR_DELIMITER = "=";
    public static final String NULL_VALUE = "null";

    public static final String AWS_LOCAL_SERVER_USER_DATA_TEMPLATE = """
                #!/bin/bash
                sudo git clone https://github.com/vadymmerezhko/SeleniumAWS.git
                cd SeleniumAWS
                sudo mvn -f local-pom.xml compile
                sudo mvn -f local-pom.xml exec:java "-DaccessKey=%s" "-DsecretKey=%s" "-DstartDate=%s" "-DtestngFile=%s" "-DthreadCount=%d" "-DtestMode=local_docker" "-Dbrowser=%s:%s"
                """.stripIndent();

    public static final String AWS_DEVICE_FARM_BROWSERS_ARM =
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:4b3efa9e-934a-4530-ad16-73ea5a12e7df";
    public static final String TEST_REPORTS_AWS_BUCKET_NAME = "selenium-aws-test-bucket";
    public static final String CONFIG_PROPERTIES_FILE_PATH = "config.properties";
    static public final String TEST_REPORT_ZIP_FILE_NAME_TEMPLATE = "test_report_%s.zip";
    static public final String TEST_REPORT_LOG_FILE_NAME_TEMPLATE = "test_output_%s.log";
    static public final String NO_FAILURES = "Failures: 0, Errors: 0";
    static public final String OPEN_AI_API_KEY_NAME = "OPEN_AI_API_KEY";
    static public final String OPEN_AI_API_URL = "https://api.openai.com/v1/chat/completions";
}
