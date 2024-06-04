package org.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command line executor class.
 */
public class CommandLineExecutor {

    /**
     * Runs command line and returns output string.
     * @param command The command line.
     * @return The output string.
     */
    public static String runCommandLine(String command) {
        StringBuilder output = new StringBuilder();

        if (SystemManager.isWindows()) {
            command = "cmd.exe /c" + command;
        }

        Runtime rt = Runtime.getRuntime();
        Process proc;
        try {
            proc = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(proc.getErrorStream()));

            // Read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                output.append(s).append('\n');
                System.out.println(s);
            }

            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                output.append(s).append('\n');
                System.out.println(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return output.toString();
    }
}
