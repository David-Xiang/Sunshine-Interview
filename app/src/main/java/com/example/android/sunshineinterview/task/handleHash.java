package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.utilities.computeHash;

public class handleHash extends AsyncTask<Object, Void, Integer> {

    @Override
    protected Integer doInBackground(Object... objects) {
        String filepath = (String)objects[0];
        String interviewID = (String)objects[1];
        int videoID = (int)objects[2];
        String hashMD5 = "";
        try {
            hashMD5 = new computeHash().GetHashcodeFromFile(filepath);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!hashMD5.equals("")){
            if (NetworkUtils.uploadHashCode(interviewID, videoID, hashMD5)){
                Log.d("", "upload hashcode succeed!");
                return new Integer(1);
            }
        }

        return new Integer(0);
    }
}
