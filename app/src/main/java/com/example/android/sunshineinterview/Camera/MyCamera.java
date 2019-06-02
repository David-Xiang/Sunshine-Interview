package com.example.android.sunshineinterview.Camera;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.android.sunshineinterview.teacheractivities.TeacherSigninActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.example.android.sunshineinterview.utilities.FileUtils.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.utilities.FileUtils.getOutputMediaFile;


public class MyCamera {
    private static final String TAG = "MyCamera";
    public Camera camera;
    private int cameraID = 1;
    private Activity mActivity;
    private TeacherSigninActivity mTeacherSinginActivity;
    private StudentSigninActivity mStudentSigninActivity;
    public static String LastSavedLoaction;


    public MyCamera(Activity activity, TeacherSigninActivity inTeacherSinginActivity, StudentSigninActivity inStudentSigninActivity) {
        mActivity = activity;
        LastSavedLoaction = null;
        mTeacherSinginActivity = inTeacherSinginActivity;
        mStudentSigninActivity = inStudentSigninActivity;

        if (camera == null){
            camera = getCamera();
        }
        setCameraParas(mActivity);
    }

    public MyCamera(Activity activity){
        mActivity = activity;
        LastSavedLoaction = null;
        mTeacherSinginActivity = null;
        mStudentSigninActivity = null;
        if (camera == null){
            camera = getCamera();
        }
        setCameraParas(mActivity);
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
            // 拍完一次照片后，预览会禁止，需要reset一下
            camera.stopPreview();
            camera.startPreview();

            if (mStudentSigninActivity != null) {
                mStudentSigninActivity.onPhotoTaken(LastSavedLoaction);
            }
            if (mTeacherSinginActivity != null) {
                mTeacherSinginActivity.onPhotoTaken(LastSavedLoaction);
            }
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
