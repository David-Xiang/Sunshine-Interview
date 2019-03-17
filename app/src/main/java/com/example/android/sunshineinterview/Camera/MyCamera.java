package com.example.android.sunshineinterview.Camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import com.example.android.sunshineinterview.teacheractivities.TeacherSigninActivity;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

public class MyCamera {
    private String info; // 教师姓名或者学生姓名等
    public Camera camera;
    private int cameraID = 1;
    private Context mContext;
    private static MyCamera mCamera;
    public String LastStoreLoction;
    
    public MyCamera(Context context, String information){
        mContext = context;
        info = information;
        LastStoreLoction = null;
        if (camera == null){
            camera = getCamera();
        }
    }

    public MyCamera(Context context){
        mContext = context;
        info = null;
        if (camera == null){
            camera = getCamera();
        }
    }

    public MyCamera()
    {
        info = null;
        if (camera == null){
            camera = getCamera();
        }
    }

    public static MyCamera getInstance() {
        if (mCamera == null) {
            mCamera = new MyCamera();
        }
        return mCamera;
    }

    public void reGetCamera()
    {
        camera = getCamera();
    }

    public void setInfo(String information){
        info = information;
    }
    
    public void releaseCamera(){
        camera.release();
        camera = null;
    }
    
    public void takePhoto(){
        camera.takePicture(null, null, mPictureCallback);
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("mPictureCallback", "called!");

            File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mediaFile == null){                                                     ////////////
                Log.d("mydebug", "failed to open the IMG file");
            }
            try{
                FileOutputStream fos = new FileOutputStream(mediaFile);
                fos.write(data);
                fos.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            LastStoreLoction = mediaFile.toString();
        }
    };

    private Camera getCamera(){
        Camera newCamera;
        try{
            newCamera = Camera.open(cameraID);
        }
        catch (Exception e)
        {
            newCamera = null;
            Log.d("mydebug", "Sorry! Can't open the camera!");
            e.printStackTrace();
        }
        return newCamera;
    }

    public void setCameraDisplayOrientation(Activity activity) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraID, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
