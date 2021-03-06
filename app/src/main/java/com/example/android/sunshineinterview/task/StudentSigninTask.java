package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.studentactivities.StudentSigninActivity;
import com.example.android.sunshineinterview.model.*;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;

public class StudentSigninTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "StudentSigninTask";
    private StudentSigninActivity mStudentSigninActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mStudentSigninActivity = (StudentSigninActivity) objects[0];
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
            mStudentSigninActivity.onStudentsUpdate(StudentSigninActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            //Interview.getInstance().updatePersonInfo();
            mStudentSigninActivity.onStudentsUpdate(StudentSigninActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mStudentSigninActivity.onStudentsUpdate(StudentSigninActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}