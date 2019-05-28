package com.example.android.sunshineinterview.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.os.Handler;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.studentactivities.StudentInProgressActivity;
import com.example.android.sunshineinterview.task.handleHash;

import java.io.File;

import static com.example.android.sunshineinterview.utilities.FileUtils.MEDIA_TYPE_VIDEO;
import static com.example.android.sunshineinterview.utilities.FileUtils.getOutputMediaFile;

public class MyMediaRecorder {
    private final static String TAG = "MyMediaRecorder";
    private MediaRecorder MR;
    private SurfaceHolder holder;
    public Camera camera;
    public boolean isRecording;
    private Handler handler;
    private String mediaFilePath;
    private Runnable runnable;
    private static int videoID;
    private static StudentInProgressActivity activity;

    public MyMediaRecorder(Context context, Camera inputCamera, SurfaceHolder inputHolder){
        MR = new MediaRecorder();
        camera = inputCamera;
        holder = inputHolder;
        handler = new Handler();
        activity = null;
        runnable=new Runnable(){
            @Override
            public void run() {
                if (MR != null) {
                    beforeStopRecord();
                }
                MR = new MediaRecorder();
                startRecord(null);
            }
        };
        mediaFilePath = "";
        isRecording = false;
    }

    public MyMediaRecorder(){
        videoID = 0;
    }

    public static void resetVideoID() {
        videoID = 0;
    }

    public void startRecord(StudentInProgressActivity studentInProgressActivity){
        Log.d(TAG, "start recording!");
        if (studentInProgressActivity != null)
        {
            activity = studentInProgressActivity;
        }
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
        Log.d(TAG, Interview.getInstance().getSide().toString());
        if (Interview.getInstance().getSide().toString().equals("STUDENT")) {
            new handleHash().execute(mediaFilePath, Interview.getInstance().getInterviewID(), videoID);
            videoID++;
            if (activity != null){
                activity.showHashResult();
            }
        }
    }

    public void stopRecord(){
        Log.d(TAG, "stop recording!");
        handler.removeCallbacks(runnable);
        beforeStopRecord();
    }

    private boolean setMediaRecorder(){
        Log.d(TAG, "start setting the MR");
        camera.unlock();
        MR.setCamera(camera);

        MR.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        MR.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        MR.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        File mediaFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mediaFile == null){
            Log.d(TAG, "failed to open the VID file");
        }
        mediaFilePath = mediaFile.toString();
        MR.setOutputFile(mediaFile.toString());

        MR.setPreviewDisplay(holder.getSurface());
        Log.d(TAG, "setting MR Okay");
        try{
            MR.prepare();
        }
        catch (Exception e){
            releaseMediaRecorder();
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "MediaRecorder failed to work");
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
