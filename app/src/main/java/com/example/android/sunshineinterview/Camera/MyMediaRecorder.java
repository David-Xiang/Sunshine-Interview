package com.example.android.sunshineinterview.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;

import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_VIDEO;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

public class MyMediaRecorder {
    public MediaRecorder mediaRecorder;
    private SurfaceHolder holder;
    public Camera camera;
    public boolean isRecording;

    public MyMediaRecorder(Context context, Camera inputCamera, SurfaceHolder inputHolder){
        mediaRecorder = new MediaRecorder();
        camera = inputCamera;
        holder = inputHolder;
        isRecording = false;
    }

    public void startRecord(){
        if (setMediaRecorder()){
            mediaRecorder.start();
            isRecording = true;
        }
        else{
            releaseMediaRecorder();
        }

    }

    public void stopRecord(){
        mediaRecorder.stop();
        releaseMediaRecorder();
        camera.lock();
        isRecording = false;
    }

    private boolean setMediaRecorder(){
        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        File mediaFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        if (mediaFile == null){
            Log.d("mydebug", "failed to open the VID file");
        }
        mediaRecorder.setOutputFile(mediaFile.toString());

        mediaRecorder.setPreviewDisplay(holder.getSurface());

        try{
            mediaRecorder.prepare();
        }
        catch (Exception e){
            releaseMediaRecorder();
            Log.d("mydebug", "MediaRecorder failed to work");
            return false;
        }
        return true;
    }

    public void releaseMediaRecorder(){
        if (mediaRecorder != null){
            mediaRecorder.reset();
            mediaRecorder.release();
            camera.lock();
            mediaRecorder = null;
        }
    }

    public void releaseCamera()
    {
        if (camera != null){
            camera.release();
            camera = null;
        }
    }
}
