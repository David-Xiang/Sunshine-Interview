package com.example.android.sunshineinterview.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.android.sunshineinterview.Camera.FindDir.MEDIA_TYPE_IMAGE;
import static com.example.android.sunshineinterview.Camera.FindDir.getOutputMediaFile;

public class MyCamera {
    private String info; // 教师姓名或者学生姓名等
    public Camera camera;
    private int cameraID = 0;
    private Context mContext;
    
    public MyCamera(Context context, String information){
        mContext = context;
        info = information;
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
            // TODO 调用下一个intent，展示拍照结果。这里只能拍照一次
            releaseCamera();
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
}
