package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileManager {

    public static synchronized void createFile(String folderPath, String fileName, String fileContent) {
        try {
            Writer fileWriter = new FileWriter(String.format("%s/%s", folderPath, fileName), false);
            BufferedWriter br = new BufferedWriter(fileWriter);
            br.write(fileContent);
            br.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
