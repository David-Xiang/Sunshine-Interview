package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.studentactivities.StudentInProgressActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.utilities.computeHash;
import com.google.gson.JsonObject;

public class handleHash extends AsyncTask<Object, Void, String> {
    public static final String TAG = "handleHash";
    StudentInProgressActivity mStudentInProgress;

    @Override
    protected String doInBackground(Object... objects) {
        String filepath = (String)objects[0];
        String interviewID = (String)objects[1];
        int videoID = (int)objects[2];
        mStudentInProgress = (StudentInProgressActivity)objects[3];
        String hashMD5 = "";
        try {
            hashMD5 = new computeHash().GetHashcodeFromFile(filepath);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!hashMD5.equals("")){
            if (NetworkUtils.uploadHashCode(interviewID, videoID, hashMD5)){
                Log.d("", "upload hashcode succeed!");
                return hashMD5;
            }
        }

        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (!result.equals("")) {
            if (mStudentInProgress != null) {
                mStudentInProgress.showHashResult(result);
            }
        }
        else {
            Log.d(TAG, "计算或上传hash值失败");
        }
    }
}
