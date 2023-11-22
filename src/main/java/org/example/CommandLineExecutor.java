package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineExecutor {

    public static String runCommandLine(String command) {
        StringBuilder output = new StringBuilder();

        if (isWindows()) {
            command = "cmd.exe /c" + command;
        }
/*        else {
            command = "/bin/sh -c" + command;
        }*/

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
                //System.out.println(s);
                output.append(s);
            }

            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                //System.out.println(s);
                output.append(s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return output.toString();
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name");
        return os.toLowerCase().startsWith("windows");
    }
}
