package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.utilities.NetworkUtils;

import java.io.File;
import java.net.URL;

public class UploadTask extends AsyncTask<String, Boolean, Boolean> {
    private static final String TAG = "uploadTask";

    @Override
    protected Boolean doInBackground(String... params) {
        String string = params[0]; // 本地完整路径
        String id = params[1];
        File file = new File(string);
        if (!file.exists()) {
            Log.v(TAG, "UploadTask() file does not exist?: " + string);
            return false;
        }
        // check exist
        String parameters = "/upload?id=" + id;
        Log.v(TAG, "UploadTask() sending url = " + parameters);
        URL url = NetworkUtils.buildUrl(parameters);
        Log.v(TAG, "Begin uploading!");
        return NetworkUtils.uploadImg(file, id, url);
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(!bool) {
            Log.w(TAG, "Something is wrong when uploading picture");
        }
    }
}
