package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.example.android.sunshineinterview.utilities.NetworkUtils;

public class DownloadTask extends AsyncTask<String, Boolean, Boolean> {
    private final static String TAG = "DownloadTask";
    @Override
    protected Boolean doInBackground(String... params) {
        //完成下载任务
        // String string = params[0];//这是从execute方法中传过来的参数, 即服务器端的路径
        Log.w(TAG, "Begin downloading!");
        String string = "https://wx1.sinaimg.cn/orj480/006u8RMBly1fxabddexv6j30qo0f0weg.jpg";
        try {
            URL url = new URL(string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //开始下载
            byte[] bytes = new byte[1024];//为方便测试故将其设置较小
            int len = -1;
            InputStream in = conn.getInputStream();
            //
            String path = new String();
            path = string.substring(string.lastIndexOf('/') + 1);
            File mediaStorageDir = new File(
                    Environment.getExternalStorageDirectory(), "SunshineInterview");
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    Log.d("mydebug", "SunshineInterview failed to create directory");
                    return false;
                }
                Log.d("mydebug", "created directory" + mediaStorageDir.getPath());
            }
            FileOutputStream out = new FileOutputStream(
                    mediaStorageDir.getPath() + File.separator + path);
            Log.w(TAG, mediaStorageDir.getPath() + File.separator + path);
            while( (len = in.read(bytes)) != -1 ){
                out.write(bytes, 0, len);
                out.flush();
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("mydebug", "downloaded a picture successfully");
        return true;
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(!bool) {
            Log.w(TAG, "Something is wrong when downloading picture");
        }
    }

}

