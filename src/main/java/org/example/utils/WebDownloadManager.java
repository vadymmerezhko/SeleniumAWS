package org.example.utils;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;

public class WebDownloadManager {

    public static void download(String fileUrl, String downloadPath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(downloadPath);
            fileOutputStream.getChannel()
                    .transferFrom(
                            Channels.newChannel(
                                    new URL(fileUrl)
                                            .openStream()), 0, Long.MAX_VALUE);
            fileOutputStream.close();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
