package com.smarte2e.helpers;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import com.smarte2e.exceptions.SmartRuntimeException;

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
            recorder.setFormat("mp4");
            recorder.setVideoCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264);
            recorder.setFrameRate(rate);
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot setup video recorder.", e);
        }
    }

    /**
     * Starts video recording.
     */
    public void start() {
        try {
            recorder.start();
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot start video recorder.", e);
        }
    }

    /**
     * Records video frame image bytes.
     * @param imageBytes The image bytes.
     */
    public void record(byte[] imageBytes) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = javax.imageio.ImageIO.read(stream);
            Frame frame = converter.convert(bufferedImage);
            recorder.record(frame);
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Video recorder failed to record video frame.", e);
        }
    }

    /**
     * Stops video recording and creates video file.
     */
    public void stop() {
        try {
            recorder.stop();
            recorder.release();
            recorder.close();
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Failed to stop video recorder.", e);
        }
    }
}
