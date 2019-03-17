package com.example.android.sunshineinterview.Camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        setZOrderOnTop(true);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Log.d("debug", "surfaceCreated");
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(180);
            mCamera.startPreview();
        }
        catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(180);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}

