package com.example.android.sunshineinterview.utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.android.sunshineinterview.model.Interview.getInterviewID;

public class FileUtils {

    public static int MEDIA_TYPE_VIDEO = 2;
    public static int MEDIA_TYPE_IMAGE = 1;

    public static boolean fileIsExists(String strFile) {
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("mydebug", "SunshineInterview failed to create directory");
                    return false;
                }
            }
            File f = new File(mediaStorageDir.getPath() + File.separator + strFile);
            return f.exists();
        } catch (Exception e) {
            return false;
        }
    }

    // API > 6.0时需要自己申请权限

    public static File getFile(String strFile) throws FileNotFoundException {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
        File f = new File(mediaStorageDir.getPath() + File.separator + strFile);
        return f;
    }

    public static File getOutputMediaFile(int type){
        // TODO 检查存储状态
        // Environment.getExternalStorageState();

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("", "SunshineInterview failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ getInterviewID() + "_" + timeStamp + ".jpg");
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + getInterviewID() + "_" + timeStamp + ".mp4");
        }
        else {
            return null;
        }
        return mediaFile;
    }
}
