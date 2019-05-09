package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.utilities.computeHash;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class handleHash extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... objects) {
        String hashMD5 = "";
        try {
            hashMD5 = new computeHash().GetHashcodeFromFile(objects[0]);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!hashMD5.equals("")){
            if (NetworkUtils.uploadHashCode(objects[0], hashMD5)){
                Log.d("", "upload hashcode succeed!");
                return new Integer(1);
            }
        }

        return new Integer(0);
    }
}
