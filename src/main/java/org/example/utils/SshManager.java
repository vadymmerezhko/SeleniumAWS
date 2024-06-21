package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * SSH manager class.
 */
@Slf4j
public class SshManager {

    private static final String SSH_TEMPLATE = "ssh -i %s %s@%s";

    /**
     * Runs SSH command.
     * @param host The host name.
     * @param user The user name.
     * @param keyFile The key file.
     * @param command The command line.
     */
    public static void runCommand(String host, String user, String keyFile, String command) {
        String ssh = String.format(SSH_TEMPLATE, keyFile, user, host);
        log.info("SSH: {}", ssh);

        try {
            Process p = Runtime.getRuntime().exec(ssh);
            PrintStream out = new PrintStream(p.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            out.println(command);

            while (in.ready()) {
                String s = in.readLine();
                System.out.println(s);
            }
            out.println("exit");

            p.waitFor();
        }
        catch (Exception e) {
            log.error("SSH error: {}", e.getMessage());
        }
    }
}
