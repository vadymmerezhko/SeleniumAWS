package org.example.utils;

import java.util.ArrayList;
import java.util.List;

public class DockerManager {

    private DockerManager() {}

    public static String stopAllContainers() {
        String result = "\n";
        String output = CommandLineExecutor.runCommandLine("docker ps -a -q");
        int size = output.length() / 12;

        for (int i = 0; i < size; i++) {
            String id = output.substring(i * 12, (i + 1)  *12);
            result += CommandLineExecutor.runCommandLine(String.format("docker stop %s", id)) + "\n";
        }
        return result;
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
                "docker run -d --net grid -e SE_EVENT_BUS_HOST=selenium-hub --shm-size=2g " +
                        "-e SE_EVENT_BUS_PUBLISH_PORT=4442 -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 " +
                        "selenium/node-%s:%s", browserName, browserVersion));
    }
}
