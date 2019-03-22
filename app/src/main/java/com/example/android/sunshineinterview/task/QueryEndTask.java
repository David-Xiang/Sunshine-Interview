package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.studentactivities.StudentInProgressActivity;
import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.android.sunshineinterview.model.*;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class QueryEndTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "QueryEndTask";
    private StudentInProgressActivity mStudentInProgressActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mStudentInProgressActivity = (StudentInProgressActivity) objects[0];
        URL url = (URL) objects[1];

//        JsonObject j = null;
//        try{
//            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
//        } catch (IOException e){
//            Log.e(TAG, "Server is not accessible.");
//            e.printStackTrace();
//        }

        String jsonString = "{\n" +
                "    \"type\": \"permission\",\n" +
                "    \"permission\": \"true\"\n" +
                "}";
        JsonObject j = new JsonParser().parse(jsonString).getAsJsonObject();
        return j;
    }

    @Override
    protected void onPostExecute(JsonObject j) {
        if (j == null
                || j.get("type").isJsonNull()
                || !j.get("type").getAsString().equals("permission")){
            mStudentInProgressActivity.onHttpResponse(StudentInProgressActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().setStatus(Interview.InterviewStatus.END);
            mStudentInProgressActivity.onHttpResponse(StudentInProgressActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mStudentInProgressActivity.onHttpResponse(StudentInProgressActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}