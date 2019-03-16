package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.teacheractivities.TeacherInProgressActivity;
import com.example.android.sunshineinterview.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;

public class QueryTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "QueryTask";
    private TeacherInProgressActivity mTeacherInProgressActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mTeacherInProgressActivity = (TeacherInProgressActivity) objects[0];
        URL url = (URL) objects[1];

        /*JsonObject j = null;
        try{
            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
        } catch (IOException e){
            Log.e(TAG, "Server is not accessible.");
            e.printStackTrace();
        }*/

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
            mTeacherInProgressActivity.onHttpResponse(TeacherInProgressActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().setSideSelected();
            Interview.getInstance().updatePersonInfo();
            mTeacherInProgressActivity.onHttpResponse(TeacherInProgressActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mTeacherInProgressActivity.onHttpResponse(TeacherInProgressActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}