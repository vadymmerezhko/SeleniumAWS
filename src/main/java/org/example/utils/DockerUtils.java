package org.example.utils;

import org.example.enums.BrowserName;

/**
 * Docker manager class.
 * Contains common Docker functionality.
 */
public final class DockerUtils {

    private DockerUtils() {}

    /**
     * Stops all Docker containers.
     * @return The result output string.
     */
    public static String stopAllContainers() {
        StringBuilder result = new StringBuilder("\n");
        String output = CommandLineUtils.runCommandLine("docker ps -a -q");
        int size = output.length() / 12;

        for (int i = 0; i < size; i++) {
            String id = output.substring(i * 12, (i + 1)  *12);
            result.append(CommandLineUtils.runCommandLine(String.format("docker stop %s", id))).append("\n");
        }
        return result.toString();
    }

    /**
     * Remotes all Docker containers.
     * @return The result output string.
     */
    public static String removeAllContainers() {
        return CommandLineUtils.runCommandLine("docker container prune -f");
    }

    /**
     * Runs Selenium hub.
     * @return The result output string.
     */
    public static String runSeleniumHub() {
        return CommandLineUtils.runCommandLine(
                "docker run -d -p 4442-4444:4442-4444 --net grid --name selenium-hub selenium/hub:latest");
    }

    /**
     * Runs Selenium node by browser name and version.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @return The result output string.
     */
    public static String runSeleniumNode(String browserName, String browserVersion) {
        String shmSize = SystemUtils.isWindows() ? "--shm-size=\"2g\"" : "";

        return CommandLineUtils.runCommandLine(String.format(
                "docker run -d --net grid -e SE_EVENT_BUS_HOST=selenium-hub %s " +
                        "-e SE_EVENT_BUS_PUBLISH_PORT=4442 -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 " +
                        "selenium/node-%s:%s", shmSize, browserName, browserVersion));
    }

    /**
     * Runs Selenium standalone by browser name and version and thread count.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @param threadCount The thread count.
     * @return The result output string.
     */
    public static String runSeleniumStandalone(BrowserName browserName, String browserVersion, int threadCount) {
        String shmSize = SystemUtils.isWindows() ? "--shm-size=\"2g\"" : "";

        return CommandLineUtils.runCommandLine(String.format(
                "docker run -e SE_NODE_MAX_SESSIONS=%d -d -p 4444:4444 -p 7900:7900 %s selenium/standalone-%s:%s",
                threadCount, shmSize, browserName, browserVersion));
    }
}
