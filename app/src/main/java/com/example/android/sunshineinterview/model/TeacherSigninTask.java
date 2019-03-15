package com.example.android.sunshineinterview.model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.teacheractivities.TeacherSigninActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;

public class TeacherSigninTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "TeacherSigninTask";
    private TeacherSigninActivity mTeacherSigninActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mTeacherSigninActivity = (TeacherSigninActivity) objects[0];
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
            mTeacherSigninActivity.onHttpResponse(TeacherSigninActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().updatePersonInfo();
            mTeacherSigninActivity.onHttpResponse(TeacherSigninActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mTeacherSigninActivity.onHttpResponse(TeacherSigninActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}