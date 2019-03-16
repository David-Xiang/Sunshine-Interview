package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.teacheractivities.ChooseOrderActivity;
import com.example.android.sunshineinterview.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;

public class ChooseOrderTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "ChooseOrderTask";
    private ChooseOrderActivity mChooseOrderActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mChooseOrderActivity = (ChooseOrderActivity) objects[0];
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
            mChooseOrderActivity.onHttpResponse(ChooseOrderActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            Interview.getInstance().setSideSelected();
            Interview.getInstance().updatePersonInfo();
            mChooseOrderActivity.onHttpResponse(ChooseOrderActivity.ServerInfo.PERMISSION);
        } else if (j.get("permission").getAsString().equals("false")) {
            mChooseOrderActivity.onHttpResponse(ChooseOrderActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}