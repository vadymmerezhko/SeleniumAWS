package org.example.utils;

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
        String shmSize = System.getProperty("os.name").startsWith("Windows") ? "\"2g\"" : "2";

        return CommandLineExecutor.runCommandLine(String.format(
                "docker run -d --net grid -e SE_EVENT_BUS_HOST=selenium-hub --shm-size=%s " +
                        "-e SE_EVENT_BUS_PUBLISH_PORT=4442 -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 " +
                        "selenium/node-%s:%s", shmSize, browserName, browserVersion));
    }

    public static String runSeleniumStandalone(String browserName, String browserVersion, int threadCount) {
        String shmSize = System.getProperty("os.name").startsWith("Windows") ? "\"2g\"" : "2";
/*        System.setProperty("SE_NODE_MAX_SESSIONS", Integer.toString(threadCount));
        System.setProperty("NODE_MAX_CONCURRENT_SESSIONS", Integer.toString(threadCount));*/

        return CommandLineExecutor.runCommandLine(String.format(
                "docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 --shm-size=%s selenium/standalone-%s:%s",
                threadCount, shmSize, browserName, browserVersion));
    }
}
