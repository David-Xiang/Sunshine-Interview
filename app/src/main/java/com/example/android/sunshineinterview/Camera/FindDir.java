package com.example.android.sunshineinterview.Camera;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FindDir {
    public static int MEDIA_TYPE_VIDEO = 2;
    public static int MEDIA_TYPE_IMAGE = 1;

    public FindDir(){}

    public static File getOutputMediaFile(int type){
        // TODO 检查存储状态
        // Environment.getExternalStorageState().

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("mydebug", "SunshineInterview failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        }
        else {
            return null;
        }
        return mediaFile;
    }
}
