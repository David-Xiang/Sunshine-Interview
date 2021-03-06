package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.android.sunshineinterview.studentactivities.WaitForTeacherConfirmActivity;
import com.example.android.sunshineinterview.teacheractivities.WaitForStudentSigninActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.android.sunshineinterview.model.*;

import java.io.IOException;
import java.net.URL;

public class QueryStartTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "QueryStartTask";
    private WaitForTeacherConfirmActivity mWaitForTeacherConfirmActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mWaitForTeacherConfirmActivity = (WaitForTeacherConfirmActivity) objects[0];
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
            mWaitForTeacherConfirmActivity.onHttpResponse(WaitForTeacherConfirmActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().setSideSelected();
            Interview.getInstance().updatePersonInfo();
            mWaitForTeacherConfirmActivity.onHttpResponse(WaitForTeacherConfirmActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mWaitForTeacherConfirmActivity.onHttpResponse(WaitForTeacherConfirmActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}