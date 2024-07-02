package org.example.helpers;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
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
            recorder.setFormat("mp4");
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            recorder.setFrameRate(rate);
        }
        catch (Exception e) {
            throw new RuntimeException("Video recorder setup exception:\n", e);
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
            throw new RuntimeException("Video recorder start exception:\n", e);
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
            throw new RuntimeException("Video frame recording exception:\n", e);
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
            throw new RuntimeException("Video recorder stopping exception:\n", e);
        }
    }
}
