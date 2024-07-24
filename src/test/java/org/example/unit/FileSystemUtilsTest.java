package org.example.unit;

import org.example.utils.FileSystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
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
    public void testCreateFileByFilePath() throws IOException {
        // Prepare
        Path tempDir = Files.createTempDirectory("testDir");
        String fileName = "testFile.txt";
        String content = "Hello TestNG";
        String filePath = String.format("%s/%s", tempDir.toString(), fileName);
        File file = new File(filePath);
        // Execute
        FileSystemUtils.createFile(file.getPath(), content);
        // Verify
        Path path = tempDir.resolve(fileName);
        Assert.assertTrue(Files.exists(path), "File should exist");
        String fileContent = Files.readString(path);
        Assert.assertEquals(fileContent, content, "Content should match");
        // Cleanup
        Files.deleteIfExists(path);
        Files.deleteIfExists(tempDir);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileByFilePathWrongPath() {
        FileSystemUtils.createFile("wrong*", "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileByFilePathNullPath() {
        FileSystemUtils.createFile(null, "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileWrongFolderPath() {
        FileSystemUtils.createFile("wrong>", "test.txt", "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileNullFolderPath() {
        FileSystemUtils.createFile(null, "test.txt", "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileWrongFileName() throws IOException {
        Path tempDir = Files.createTempDirectory("testDir");
        FileSystemUtils.createFile(tempDir.toString(), "wrong*", "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileBlankFileName() throws IOException {
        Path tempDir = Files.createTempDirectory("testDir");
        FileSystemUtils.createFile(tempDir.toString(), "", "Some content");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testCreateFileNullContent() throws IOException {
        Path tempDir = Files.createTempDirectory("testDir");
        FileSystemUtils.createFile(tempDir.toString(), "test.txt", null);
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

    @Test(expectedExceptions = RuntimeException.class)
    public void testReadFileWrongFilePath() {
        FileSystemUtils.readFile("wrong*");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testReadFileBlankFilePath() {
        FileSystemUtils.readFile(" ");
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

    @Test(expectedExceptions = RuntimeException.class)
    public void testDeleteFileWrongFilePath() {
        FileSystemUtils.deleteFile("wrong*");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testDeleteFileBlankFilePath() {
        FileSystemUtils.deleteFile("");
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

    @Test(expectedExceptions = RuntimeException.class)
    public void testFileWrongFilePathExists() {
        FileSystemUtils.fileExists("wrong*");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testFileBlankFilePathExists() {
        FileSystemUtils.fileExists(" ");
    }
}
