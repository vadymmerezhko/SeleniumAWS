package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

@Slf4j
public class SshManager {

    private static final String SSH_TEMPLATE = "ssh -i %s %s@%s";

    public static void runCommand(String host, String user, String keyFile, String command) {
        String ssh = String.format(SSH_TEMPLATE, keyFile, user, host);
        log.debug("SSH: {}", ssh);

        try {
            Process p = Runtime.getRuntime().exec(ssh);
            PrintStream out = new PrintStream(p.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            out.println(command);

            while (in.ready()) {
                String s = in.readLine();
            }
            out.println("exit");

            p.waitFor();
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
