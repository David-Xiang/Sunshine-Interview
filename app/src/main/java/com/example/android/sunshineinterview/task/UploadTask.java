package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class UploadTask extends AsyncTask<String, Boolean, Boolean> {
    private static final String TAG = "uploadTask";

    @Override
    protected Boolean doInBackground(String... params) {
        String string = params[0]; // 本地完整路径
        File file = new File(string);
        Log.w(TAG, "Begin uploading!");

        // TODO: finish name and url
        // url: http://serverip/upload?xxxx?
        return NetworkUtils.uploadImg(file, name, url);
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if(!bool) {
            Log.w(TAG, "Something is wrong when uploading picture");
        }
    }
}
