package com.example.android.sunshineinterview.utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "SunshineInterview");
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("mydebug", "SunshineInterview failed to create directory");
                    return false;
                }
            }
            File f = new File(mediaStorageDir.getPath() + File.separator + strFile);
            return f.exists();
        } catch (Exception e)
        {
            return false;
        }
    }

    // API > 6.0时需要自己申请权限
}