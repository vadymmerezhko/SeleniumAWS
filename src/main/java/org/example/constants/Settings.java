package org.example.constants;

import com.amazonaws.regions.Regions;

public class Settings {
    public static final int SELENIUM_SERVERS_COUNT = 1;
    public static final Regions AWS_REGION = Regions.US_WEST_1;
    public static final String AWS_DOCKER_IMAGE_ID = "ami-0d93c31a4c91fcd51";
    public static final String AWS_RMI_IMAGE_ID = "ami-05188da1ca88643c3";
    public static final String AWS_LOCAL_RUN_IMAGE_ID = "ami-05188da1ca88643c3";

    public static final String AWS_EC2_USER_DATA_TEMPLATE =
            "#!/bin/bash\n" +
            "sudo docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 " +
            "--shm-size=\"2g\" selenium/standalone-%s:%s";
    public static final String SECURITY_KEY_PAIR_NAME = "SeleniumKeyPair";
    public static final String SECURITY_GROUP_NAME = "Selenium Test Security Group";
    public static int TEST_RETRY_LIMIT = 3;
    public static final int AWS_URL_EXPIRES_SECONDS = 60 * 15;
    public static final int RMI_SERVER_BASE_PORT = 4200;
    public static final int REMOTE_WEB_DRIVER_PORT = 4444;
    public static final int SERVER_WAIT_TIMEOUT_SECONDS = 180;
    public static final String RMI_SERVER_USER_DATA_TEMPLATE =
            "#!/bin/bash\n" +
            "sudo git clone https://github.com/vadymmerezhko/SeleniumAWS.git\n" +
            "cd SeleniumAWS\n" +
            "sudo mvn -f rmi-pom.xml compile\n" +
            "sudo mvn -f rmi-pom.xml exec:java \"-DthreadCount=%d\" \"-DtestMode=local_docker\" \"-Dbrowser=%s:%s\"";

    public static final String AWS_LOCAL_SERVER_USER_DATA_TEMPLATE =
            "#!/bin/bash\n" +
            "sudo git clone https://github.com/vadymmerezhko/SeleniumAWS.git\n" +
            "cd SeleniumAWS\n" +
            "sudo mvn -f local-pom.xml compile\n" +
            "sudo mvn -f local-pom.xml exec:java " +
            "\"-DaccessKey=%s\" \"-DsecretKey=%s\" \"-DstartDate=%s\" \"-DtestngFile=%s\" " +
            "\"-DthreadCount=%d\" \"-DtestMode=local_docker\" \"-Dbrowser=%s:%s\"";

    public static final String AWS_DEVICE_FARM_BROWSERS_ARM =
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:4b3efa9e-934a-4530-ad16-73ea5a12e7df";
    public static final String AWS_LAMBDA_FUNCTION_ARN =
            "arn:aws:lambda:us-west-1:535905549021:function:seleniumTestLambda";
    public static final String TEST_REPORTS_AWS_BUCKET_NAME = "selenium-aws-test-bucket";
    public static final String RMI_SERVER_NAME = "RmiTestServer";
    public static final String REQUEST_HANDLER_ERROR_MSG = "Request handler error";
    public static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
    static public final String TEST_REPORT_ZIP_FILE_NAME_TEMPLATE = "test_report_%s.zip";
    static public final String TEST_REPORT_LOG_FILE_NAME_TEMPLATE = "test_output_%s.log";
    static public final String NO_FAILURES = "Failures: 0, Errors: 0";
}
