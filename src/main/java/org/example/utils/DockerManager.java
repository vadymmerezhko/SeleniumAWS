package org.example.utils;

public class DockerManager {

    private DockerManager() {}

    public static String stopAllContainers() {
        StringBuilder result = new StringBuilder("\n");
        String output = CommandLineExecutor.runCommandLine("sudo docker ps -a -q");
        int size = output.length() / 12;

        for (int i = 0; i < size; i++) {
            String id = output.substring(i * 12, (i + 1)  *12);
            result.append(CommandLineExecutor.runCommandLine(String.format("sudo docker stop %s", id))).append("\n");
        }
        return result.toString();
    }

    public static String removeAllContainers() {
        return CommandLineExecutor.runCommandLine("sudo docker container prune -f");
    }

    public static String runSeleniumHub() {
        return CommandLineExecutor.runCommandLine(
                "docker run -d -p 4442-4444:4442-4444 --net grid --name selenium-hub selenium/hub:latest");
    }

    public static String runSeleniumNode(String browserName, String browserVersion) {
        String shmSize = SystemManager.isWindows() ? "--shm-size=\"2g\"" : "";

        return CommandLineExecutor.runCommandLine(String.format(
                "docker run -d --net grid -e SE_EVENT_BUS_HOST=selenium-hub %s " +
                        "-e SE_EVENT_BUS_PUBLISH_PORT=4442 -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 " +
                        "selenium/node-%s:%s", shmSize, browserName, browserVersion));
    }

    public static String runSeleniumStandalone(String browserName, String browserVersion, int threadCount) {
        String shmSize = SystemManager.isWindows() ? "--shm-size=\"2g\"" : "";

        return CommandLineExecutor.runCommandLine(String.format(
                "docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 %s selenium/standalone-%s:%s",
                threadCount, shmSize, browserName, browserVersion));
    }
}
