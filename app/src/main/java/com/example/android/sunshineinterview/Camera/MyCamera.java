package com.example.android.sunshineinterview.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Image;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import com.example.android.sunshineinterview.teacheractivities.TeacherSigninActivity;
import com.example.myapplication.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

public class MyCamera {
    private String info; // 教师姓名或者学生姓名等
    public Camera camera;
    private int cameraID = 1;
    private Context mContext;
    private static MyCamera mCamera;
    private ImageView showPhoto;
    public static String LastSavedLoaction;


    public MyCamera(Context context, ImageView i){
        showPhoto = i;
        mContext = context;
        LastSavedLoaction = null;
        info = null;
        if (camera == null){
            camera = getCamera();
        }
    }

    public MyCamera(Context context){
        mContext = context;
        info = null;
        LastSavedLoaction = null;
        if (camera == null){
            camera = getCamera();
        }
    }

    public MyCamera()
    {
        info = null;
        LastSavedLoaction = null;
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
    
    public void releaseCamera(){
        camera.release();
        camera = null;
    }

    public void resetCamera(){
        camera = getCamera();
    }

    public Camera AcquireCamera(){
        return camera;
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

            LastSavedLoaction = mediaFile.toString();
            try {
                FileInputStream fis = new FileInputStream(mediaFile);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                // options.inSampleSize = 10;
                Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
                showPhoto.setImageBitmap(btp);
                fis.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

            camera.stopPreview();
            camera.startPreview();
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
