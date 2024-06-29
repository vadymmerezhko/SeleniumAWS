package org.example.utils;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class VideoRecorder {
    private FFmpegFrameRecorder recorder;
    private Java2DFrameConverter converter;

    /**
     * Setups video recorder data.
     * @param videoFilePath The video file path.
     * @param width The frame width.
     * @param height The frame height.
     * @param rate The frame rate per second.
     */
    public void setup(String videoFilePath, int width, int height, int rate) {
        try {
            recorder = new FFmpegFrameRecorder(videoFilePath, width, height);
            converter = new Java2DFrameConverter();
            recorder.setFrameRate(rate);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
            recorder.setVideoBitrate(9000);
            recorder.setFormat("mp4");
            recorder.setVideoQuality(0);
        }
        catch (Exception e) {
            throw new RuntimeException("Video recorder setup exception:\n", e);
        }
    }

    public void start() {
        try {
            recorder.start();
        }
        catch (Exception e) {
            throw new RuntimeException("Video recorder start exception:\n", e);
        }
    }

    public void record(byte[] imageBytes) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = javax.imageio.ImageIO.read(stream);
            Frame frame = converter.convert(bufferedImage);
            recorder.record(frame);
        }
        catch (Exception e) {
            throw new RuntimeException("Video frame recording exception:\n", e);
        }
    }

    public void stop() {
        try {
            recorder.stop();
            recorder.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Video recorder stopping exception:\n", e);
        }
    }
}
