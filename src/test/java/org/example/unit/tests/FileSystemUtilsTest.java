package org.example.unit.tests;

import org.example.utils.FileSystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FileSystemUtilsTest {

    @Test
    public void testCreateFile() throws IOException {
        // Prepare
        Path tempDir = Files.createTempDirectory("testDir");
        String fileName = "testFile.txt";
        String content = "Hello TestNG";

        // Execute
        FileSystemUtils.createFile(tempDir.toString(), fileName, content);

        // Verify
        Path file = tempDir.resolve(fileName);
        Assert.assertTrue(Files.exists(file), "File should exist");
        String fileContent = Files.readString(file);
        Assert.assertEquals(fileContent, content, "Content should match");

        // Cleanup
        Files.deleteIfExists(file);
        Files.deleteIfExists(tempDir);
    }

    @Test
    public void testReadFile() throws IOException {
        // Prepare
        Path tempFile = Files.createTempFile("testRead", ".txt");
        String expectedContent = "Read test content";
        Files.writeString(tempFile, expectedContent);

        // Execute
        String actualContent = FileSystemUtils.readFile(tempFile.toString());

        // Verify
        Assert.assertEquals(actualContent, expectedContent, "The content read should match the content written");

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testDeleteFile() throws IOException {
        // Prepare
        Path tempFile = Files.createTempFile("testDelete", ".txt");

        // Execute
        FileSystemUtils.deleteFile(tempFile.toString());

        // Verify
        Assert.assertFalse(Files.exists(tempFile), "File should be deleted");
    }

    @Test
    public void testFileExists() throws IOException {
        // Prepare
        Path tempFile = Files.createTempFile("testExists", ".txt");

        // Execute & Verify
        Assert.assertTrue(FileSystemUtils.fileExists(tempFile.toString()), "File should exist");

        // Cleanup
        Files.deleteIfExists(tempFile);
    }
}
