package com.example.android.sunshineinterview.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.os.Handler;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.task.handleHash;

import java.io.File;

import static com.example.android.sunshineinterview.utilities.FileUtils.MEDIA_TYPE_VIDEO;
import static com.example.android.sunshineinterview.utilities.FileUtils.getOutputMediaFile;

public class MyMediaRecorder {
    private MediaRecorder MR;
    private SurfaceHolder holder;
    public Camera camera;
    public boolean isRecording;
    private Handler handler;
    private String mediaFilePath;
    private Runnable runnable;
    private static int videoID;

    public MyMediaRecorder(Context context, Camera inputCamera, SurfaceHolder inputHolder){
        MR = new MediaRecorder();
        camera = inputCamera;
        holder = inputHolder;
        handler = new Handler();
        runnable=new Runnable(){
            @Override
            public void run() {
                if (MR != null) {
                    beforeStopRecord();
                }
                MR = new MediaRecorder();
                startRecord();
            }
        };
        mediaFilePath = "";
        isRecording = false;
    }

    public MyMediaRecorder(){
        videoID = 0;
    }

//    public void resetVideoID(){
//        videoID = 0;
//    }

    public void startRecord(){
        Log.d("videoDebug", "start recording!");
        if (setMediaRecorder()){
            MR.start();
            isRecording = true;
            handler.postDelayed(runnable, 60000);
        }
        else{
            releaseMediaRecorder();
        }
    }

    public void beforeStopRecord(){
        MR.setOnErrorListener(null);
        MR.setOnInfoListener(null);
        MR.setPreviewDisplay(null);
        MR.stop();
        releaseMediaRecorder();
        isRecording = false;

        // handle hash
        Log.d("videoDebug", Interview.getInstance().getSide().toString());
        if (Interview.getInstance().getSide().toString().equals("STUDENT")) {
            new handleHash().execute(mediaFilePath, Interview.getInstance().getInterviewID(), videoID);
            videoID++;
        }
    }

    public void stopRecord(){
        Log.d("videoDebug", "stop recording!");
        handler.removeCallbacks(runnable);
        beforeStopRecord();
    }

    private boolean setMediaRecorder(){
        Log.d("videoDebug", "start setting the MR");
        camera.unlock();
        MR.setCamera(camera);

        MR.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        MR.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //MR.setMaxDuration(10000);//设置视频的最大持续时间
        //MR.setMaxFileSize(1*1024*1024*1024);

        MR.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        File mediaFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mediaFile == null){
            Log.d("videoDebug", "failed to open the VID file");
        }
        mediaFilePath = mediaFile.toString();
        MR.setOutputFile(mediaFile.toString());

        MR.setPreviewDisplay(holder.getSurface());
        Log.d("videoDebug", "setting MR Okay");
        try{
            MR.prepare();
        }
        catch (Exception e){
            releaseMediaRecorder();
            Log.d("videoDebug", e.getMessage());
            Log.d("videoDebug", "MediaRecorder failed to work");
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (MR != null){
            MR.reset();
            MR.release();
            camera.lock();
            MR = null;
        }
    }
}
