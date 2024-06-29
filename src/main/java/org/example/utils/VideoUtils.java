package org.example.utils;


import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.util.List;

import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;

public class VideoUtils {

    private VideoUtils() {}

    public static void convertJPGtoMovie(List<String> links, String videoPath)
    {
        OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoPath,640,720);
        try {
            recorder.setFrameRate(1);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
            recorder.setVideoBitrate(9000);
            recorder.setFormat("mp4");
            recorder.setVideoQuality(0); // maximum quality
            recorder.start();

            for (String link : links) {
                recorder.record(grabberConverter.convert(cvLoadImage(link)));
            }
            recorder.stop();
            grabberConverter.close();
            recorder.close();
        }
        catch (org.bytedeco.javacv.FrameRecorder.Exception e){
            e.printStackTrace();
        }
    }
}
