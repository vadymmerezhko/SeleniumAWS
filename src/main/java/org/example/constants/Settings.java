package org.example.constants;

public class Settings {
    static public final int SELENIUM_SERVERS_COUNT = 1;
    public static final int TEST_RETRY_LIMIT = 4;
    static public final String AWS_DOCKER_IMAGE_ID = "ami-0d93c31a4c91fcd51";
    static public final String SECURITY_KEY_PAIR_NAME = "SeleniumKeyPair";
    static public final String SECURITY_GROUP_NAME = "Selenium Test Security Group";
    static public final int AWS_URL_EXPIRES_SECONDS = 60 * 15;
    public static final int RMI_REGISTRY_PORT = 4200;
    static public final String AWS_DEVICE_FARM_BROWSERS_ARM =
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:4b3efa9e-934a-4530-ad16-73ea5a12e7df";
    static public final String AWS_LAMBDA_FUNCTION_ARN =
            "arn:aws:lambda:us-west-1:535905549021:function:seleniumTestLambda";
    public static final String RMI_SERVER_NAME = "RmiTestServer";
    public static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
}
