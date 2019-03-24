package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.studentactivities.WaitForChooseOrderActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class QueryTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "QueryTask";
    private WaitForChooseOrderActivity mWaitForChooseOrderActivity;

    @Override
    protected JsonObject doInBackground(Object... objects) {
        mWaitForChooseOrderActivity = (WaitForChooseOrderActivity) objects[0];
        URL url = (URL) objects[1];

//        JsonObject j = null;
//        try{
//            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
//        } catch (IOException e){
//            Log.e(TAG, "Server is not accessible.");
//            e.printStackTrace();
//        }

        String jsonString = "{\n" +
                "    \"type\": \"site_info\",\n" +
                "    \"permission\": \"true\",\n" +
                "    \"info\": {\n" +
                "        \"order\": \"01\"\n" +
                "    }\n" +
                "}";
        JsonObject j = new JsonParser().parse(jsonString).getAsJsonObject();
        return j;
    }

    @Override
    protected void onPostExecute(JsonObject j) {
        if (j == null
                || j.get("type").isJsonNull()
                || !j.get("type").getAsString().equals("site_info")){
            Log.e(TAG, "onPostExecute(): fault 1");
            mWaitForChooseOrderActivity.onHttpResponse(WaitForChooseOrderActivity.ServerInfo.NOACCESS, null);
        } else if (j.get("permission").getAsString().equals("true")) {
            String order = j.getAsJsonObject("info").get("order").getAsString();
            mWaitForChooseOrderActivity.onHttpResponse(WaitForChooseOrderActivity.ServerInfo.PERMISSION, order);
        } else if (j.get("permission").getAsString().equals("false")) {
            Log.e(TAG, "onPostExecute(): fault 2");
            mWaitForChooseOrderActivity.onHttpResponse(WaitForChooseOrderActivity.ServerInfo.REJECTION, null);
        } else {
            Log.e(TAG, "onPostExecute(): Something is wrong");
        }
    }
}