package com.example.android.sunshineinterview.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.example.android.sunshineinterview.utilities.FileUtils.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.utilities.FileUtils.getOutputMediaFile;


public class MyCamera {
    private static final String TAG = "MyCamera";
    public Camera camera;
    private int cameraID = 1;
    private Activity mActivity;
    private static MyCamera mCamera;
    private ImageView showPhoto;
    public static String LastSavedLoaction;


    public MyCamera(Activity activity, ImageView i){
        showPhoto = i;
        mActivity = activity;
        LastSavedLoaction = null;
        if (camera == null){
            camera = getCamera();
        }
        setCameraParas(mActivity);
    }

    public MyCamera(Activity activity){
        mActivity = activity;
        LastSavedLoaction = null;
        if (camera == null){
            camera = getCamera();
        }
        setCameraParas(mActivity);
    }

    public MyCamera()
    {
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


    public Camera AcquireCamera(){
        return camera;
    }
    
    public void takePhoto(){
        camera.takePicture(null, null, mPictureCallback);
    }

    private Bitmap convertBmp(Bitmap bm){
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            // 回收空间！
            bm.recycle();
        }
        return returnBm;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "mPictureCallback called!");

            File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (mediaFile == null){                                                     ////////////
                Log.d(TAG, "failed to open the IMG file");
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
                showPhoto.setImageBitmap(convertBmp(btp));
                // showPhoto.setImageBitmap(btp);
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
            Log.d(TAG, "Sorry! Can't open the camera!");
            e.printStackTrace();
        }
        return newCamera;
    }

    private void setCameraParas(Activity activity) {
        setCameraDisplayOrientation(activity);

        Camera.Parameters paras = camera.getParameters();
        List<Camera.Size> picSize = paras.getSupportedPictureSizes();
        Camera.Size minSize = picSize.get(0);
        Log.d(TAG, String.valueOf(minSize.width) + String.valueOf(minSize.height));
        paras.setPictureSize(minSize.width, minSize.height);

        camera.setParameters(paras);
    }

    private void setCameraDisplayOrientation(Activity activity) {
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
