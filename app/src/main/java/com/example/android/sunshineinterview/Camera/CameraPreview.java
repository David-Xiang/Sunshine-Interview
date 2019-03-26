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
        if (mCamera != null)
            Log.d("mydebug", "mCamera initialized!");
    }

    public void resetCamera(Camera cmr)
    {
        mCamera = cmr;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("mydebug", "surfaceCreated");

        try {
            if (mCamera == null){
                Log.d("videoDebug", "mCamera == null");
                return;
            }
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (IOException e) {
            Log.v(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("mydebug", "surfaceDestroyed");
            if (mCamera != null){
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);

            // 这里会释放相机
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d("mydebug", "surfaceChanged");
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        }
        catch (Exception e){}

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        }
        catch (Exception e){ }
    }
}

