package com.bilibili.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
/**
 * 设置消息监听
 * 监听组：监听topic：监听tag(默认监听topic下所有)
 * 监听消费模式:默认负载均衡：CLUSTERING（每一个消息只发给一个消费者）、广播模式：BROADCASTING（发送给所有消费者）
 *
 */
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.consumer.topic}")
public class ConvertService implements RocketMQListener<String>{

//        转码后的音频文件位置
    @Value("${base.param.tempLocation}")
    private String tempLoc;
    @Value("${base.param.fileLocation}")
    private String fileLoc;

    @Override
    public void onMessage(String message) {
        System.out.println(message);
        if (1==1)
        return;
//        FFmpegProcessor.convertMediaToM3u8ByHttp(inputStream, m3u8Url, infoUrl);
        doConversions(new File(tempLoc+"/"+message+"/complete.mp4"), message);
    }

//        视频转码,  不好用
    public  void convertMediaToM3u8ByHttp(InputStream inputStream, String m3u8Url, String infoUrl) throws IOException {

        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        FFmpegLogCallback.set();

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        grabber.start();

        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(m3u8Url, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());

        recorder.setFormat("hls");
        recorder.setOption("hls_time", "5");
        recorder.setOption("hls_list_size", "0");
        recorder.setOption("hls_flags", "delete_segments");
        recorder.setOption("hls_delete_threshold", "1");
        recorder.setOption("hls_segment_type", "mpegts");
        recorder.setOption("hls_segment_filename", "C:\\Users\\79301\\Desktop\\upload\\test-%d.ts");
        recorder.setOption("hls_key_info_file", infoUrl);

        // http属性
        recorder.setOption("method", "POST");

        recorder.setFrameRate(25);
        recorder.setGopSize(2 * 25);
        recorder.setVideoQuality(1.0);
        recorder.setVideoBitrate(10 * 1024);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.start();

        Frame frame;
        while ((frame = grabber.grabImage()) != null) {
            try {
                recorder.record(frame);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        recorder.setTimestamp(grabber.getTimestamp());
        recorder.close();
        grabber.close();
    }
    public  void doConversions(File videoFile, String videoName) {

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoFile);

        try {
            String name = videoFile.getName().split("\\.")[0];
            File file = new File(fileLoc +"/"+ videoName);
            if(!file.exists() && !file.isDirectory())
                file.mkdir();
            frameGrabber.start();
            Frame grab;
            FrameRecorder recorder = new FFmpegFrameRecorder(new File( fileLoc +"/"+ videoName + "/" + videoName + ".m3u8"), frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
//            recorder.setAspectRatio((double) 16/9);
//            recorder.setFrameRate(frameGrabber.getFrameRate());
//            recorder.setGopSize((int)frameGrabber.getFrameRate()*2);
//            recorder.setVideoQuality(1);
//            recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//            recorder.setOption("vf", "setdar=16:9");
//            recorder.setOption("hls_time", "3");
//            recorder.setOption("start_number", "0");
//            recorder.setOption("hls_list_size", "0");
//            recorder.setOption("f", "hls");
//            recorder.setOption("level", "3.0");
//            recorder.setOption("profile:v", "baseline");
//            recorder.setOption("strict", "experimental");
//            recorder.setOption("hls_key_info_file", "C:\\Users\\79301\\Desktop\\test.info");
            recorder.start();
            while ((grab = frameGrabber.grab()) != null) {
                recorder.record(grab);
            }
            recorder.stop();
            recorder.release();
            frameGrabber.stop();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception ignored) {
            System.out.println(ignored.getMessage());
        }}
}
