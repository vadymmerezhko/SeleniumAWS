package org.example.unit;

import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ZipFileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZipFileUtilsTest {
    @Test
    public void testUnzipValid() throws IOException {
        // Assuming there is a valid zip file at "src/test/resources/test.zip"
        Path zippedFile = Files.createTempFile("test", ".zip");
        Path unzippedFolder = Files.createTempDirectory("unzip");
        File file = new File(unzippedFolder.toFile(), "file.txt");
        file.createNewFile();

        // Simulate a simple zip process or prepare a test zip file beforehand
        ZipFileUtils.zip(unzippedFolder.toString(), zippedFile.toString());

        // Test unzip
        ZipFileUtils.unzip(zippedFile.toString(), unzippedFolder.toString());
        Assert.assertTrue(Files.exists(unzippedFolder.resolve("file.txt")));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testUnzipInvalidPath() {
        // Pass invalid paths
        ZipFileUtils.unzip("does_not_exist.zip", "invalid_unzip_path");
    }

    @Test
    public void testUnzipWithPasswordValid() throws IOException {
        // Assuming there is a valid zip file at "src/test/resources/test.zip"
        Path zippedFile = Files.createTempFile("test", ".zip");
        Path unzippedFolder = Files.createTempDirectory("unzip");
        File file = new File(unzippedFolder.toFile(), "file.txt");
        file.createNewFile();
        String password = "strongPassw0rd-156";

        // Simulate a simple zip process or prepare a test zip file beforehand
        ZipFileUtils.zip(unzippedFolder.toString(), zippedFile.toString(), password);

        // Test unzip
        ZipFileUtils.unzip(zippedFile.toString(), unzippedFolder.toString(), password);
        Assert.assertTrue(Files.exists(unzippedFolder.resolve("file.txt")));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testUnzipWithWrongPassword() throws IOException {
        // Assuming there is a valid zip file at "src/test/resources/test.zip"
        Path zippedFile = Files.createTempFile("test", ".zip");
        Path unzippedFolder = Files.createTempDirectory("unzip");
        File file = new File(unzippedFolder.toFile(), "file.txt");
        file.createNewFile();
        String password = "strongPassw0rd-156";

        // Simulate a simple zip process or prepare a test zip file beforehand
        ZipFileUtils.zip(unzippedFolder.toString(), zippedFile.toString(), password);

        // Test unzip
        ZipFileUtils.unzip(zippedFile.toString(), unzippedFolder.toString(), "wrongPassword");
    }

    @Test
    public void testZipFolderValid() throws IOException {
        // Paths for the zip operation
        Path folderToZip = Files.createTempDirectory("toZip");
        Path resultingZip = Files.createTempFile("result", ".zip");

        // Simulate creating files in the folder
        Files.createFile(folderToZip.resolve("file1.txt"));
        Files.createFile(folderToZip.resolve("file2.txt"));

        // Test zipping the folder
        ZipFileUtils.zip(folderToZip.toString(), resultingZip.toString());
        Assert.assertTrue(Files.exists(resultingZip) && Files.size(resultingZip) > 0);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testZipFolderInvalid() {
        // Test zipping a non-existing folder
        ZipFileUtils.zip("non_existent_folder", "result.zip");
    }
}
