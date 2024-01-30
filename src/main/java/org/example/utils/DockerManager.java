package org.example.utils;

public class DockerManager {

    private DockerManager() {}

    public static String stopAllContainers() {
        return CommandLineExecutor.runCommandLine("docker stop $(docker ps -a -q)");
    }

    public static String removeAllContainers() {
        return CommandLineExecutor.runCommandLine("docker container prune -f");
    }

    public static String runSeleniumHub() {
        return CommandLineExecutor.runCommandLine(
                "docker run -d -p 4442-4444:4442-4444 --net grid --name selenium-hub selenium/hub:latest");
    }

    public static String runSeleniumNode(String browserName, String browserVersion) {
        return CommandLineExecutor.runCommandLine(String.format(
                "docker run -d --net grid -e SE_EVENT_BUS_HOST=selenium-hub --shm-size=\"2g\" " +
                        "-e SE_EVENT_BUS_PUBLISH_PORT=4442 -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 " +
                        "selenium/node-%s:%s", browserName, browserVersion));
    }
}
