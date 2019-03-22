package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.commonactivities.ChooseSideActivity;
import com.example.android.sunshineinterview.model.*;
import com.google.gson.JsonObject;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class ChooseSideTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "ChoooseSideTask";
    private ChooseSideActivity mChooseSideActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mChooseSideActivity = (ChooseSideActivity) objects[0];
        URL url = (URL) objects[1];

//        JsonObject j = null;
//        try{
//            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
//        } catch (IOException e) {
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
        Log.i(TAG, j.toString());
        if (j == null
                || j.get("type").isJsonNull()
                || !j.get("type").getAsString().equals("permission")){
            mChooseSideActivity.onHttpResponse(ChooseSideActivity.ServerInfo.NOACCESS);
        } else if (j.get("permission").getAsString().equals("true")) {
            mChooseSideActivity.onHttpResponse(ChooseSideActivity.ServerInfo.PERMISSION);
            Interview.getInstance().setSideSelected();
        } else if (j.get("permission").getAsString().equals("false")) {
            mChooseSideActivity.onHttpResponse(ChooseSideActivity.ServerInfo.REJECTION);
        } else {
            Log.e(TAG, "Something is wrong in PoseExecute");
        }
    }
}