package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.android.sunshineinterview.teacheractivities.WaitForStudentSigninActivity;
import com.example.android.sunshineinterview.model.*;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class StartTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "StartTask";
    private WaitForStudentSigninActivity mWaitForStudentSigninActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mWaitForStudentSigninActivity = (WaitForStudentSigninActivity) objects[0];
        URL url = (URL) objects[1];

        JsonObject j = null;
        try{
            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
        } catch (IOException e){
            Log.e(TAG, "Server is not accessible.");
            e.printStackTrace();
        }

//        String jsonString = "{\n" +
//                "    \"type\": \"permission\",\n" +
//                "    \"permission\": \"true\"\n" +
//                "}";
//        JsonObject j = new JsonParser().parse(jsonString).getAsJsonObject();
        return j;
    }

    @Override
    protected void onPostExecute(JsonObject j) {
        if (j == null
                || j.get("type").isJsonNull()
                || !j.get("type").getAsString().equals("permission")){
            mWaitForStudentSigninActivity.onHttpResponse(WaitForStudentSigninActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().setSideSelected();
            Interview.getInstance().updatePersonInfo();
            mWaitForStudentSigninActivity.onHttpResponse(WaitForStudentSigninActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mWaitForStudentSigninActivity.onHttpResponse(WaitForStudentSigninActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}