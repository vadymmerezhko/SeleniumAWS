package org.example.constants;

public class Settings {
    static public final String PROJECT_FOLDER_PATH = "/home/ec2-user/test";
    static public final String PROJECT_NAME = "SeleniumAWS";
    static public final long SELENIUM_SERVERS_COUNT = 1;
    static public final int DEFAULT_THREADS_COUNT = 4;

    public static final int TEST_RETRY_LIMIT = 4;
    static public final String AWS_IMAGE_ID = "ami-0e26e576b4517b30d";
    static public final String SECURITY_KEY_PAIR_NAME = "SeleniumKeyPair";
    static public final String SECURITY_GROUP_NAME = "Selenium Test Security Group";
    static public final String AMS_ACCESS_KEY_SECRET_NAME = "AWS_EC2_Acees_Key";

    static public final String AWS_DEVICE_FARM_BROWSERS_ARM =
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:4b3efa9e-934a-4530-ad16-73ea5a12e7df";
    static public final String[] AWS_DEVICE_FARM_BROWSER_PROJECT_ARN = {
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:eb747a27-5f10-41a2-a39b-ed1d7eaeb05c",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:8441c278-ce73-4a99-9732-32d27d5cbcc4",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:b320788d-5821-4384-ad1c-9a8e5dadb91f",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:b154c8b5-0706-425a-9d81-5b5bde3a8a5c",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:3cc9414a-e1c1-4e78-ad0f-a3ba1642f952",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:730b3a8c-af28-4554-b978-d1ddc9cc7e32",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:fc6398ba-6f67-4393-945a-fa40ce477006",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:b89a23ee-099a-4c35-88dc-c78a443558ae",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:c1e42e1e-6ac9-49f6-b030-9982c1578c17",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:057b2f27-2fbf-4849-89bc-82baf93deca4",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:2063a7ae-d4f8-4d3d-84f6-e80a3cd78b78",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:57b51626-fa9e-4d03-ada7-878e56dae87c",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:c050ee6b-8f0d-4241-82f2-8258937de322",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:07ae8c4b-e045-4f87-abfc-0726ef6ac70b",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:749982de-897c-44e6-96f7-6e39300420b4",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:3f53f0de-4222-4aad-a65a-f644866f3ee1",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:c1330642-c217-47cc-b0ac-062b5402a7d2",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:111399eb-7772-4623-8e37-934597ff093e",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:cb9408e7-0e36-4933-912a-f21f9c31830c",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:fab2c52d-73a4-42d9-9d0a-646f8c47849e",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:f6446e6c-068b-42ca-b8b5-f75293770d3d",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:9ff2118b-3ff1-4f0c-98f8-afc8e3affac3",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:43547323-4405-40c4-81c4-a8d4ead5f181",
            "arn:aws:devicefarm:us-west-2:535905549021:testgrid-project:fe2a0fcc-26e1-4237-b929-33d6b71095bf"};

    static public final String[] AWS_DEVICE_FARM_BROWSERS = {"chrome", "firefox"};
    static public final String[] AWS_DEVICE_FARM_BROWSER_VERSIONS = {"latest", "latest-1", "latest-2"};
    static public final String USER_DATA = "IyEvYmluL2Jhc2gKY2QgaG9tZS8KY2QgZWMyLXVzZXIKamF2YSAtam" +
            "FyIHNlbGVuaXVtLXNlcnZlci00LjE0LjEuamFyIHN0YW5kYWxvbmUgLS1zZWxlbml1bS1tYW5hZ2VyIHRydWUgJgo=";
}
