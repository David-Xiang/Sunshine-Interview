package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.commonactivities.ValidateActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.example.android.sunshineinterview.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class ValidateTask extends AsyncTask<Object, Void, JsonObject> {
    private final static String TAG = "ValidateTask";
    private ValidateActivity mValidateActivity;
    @Override
    protected JsonObject doInBackground(Object... objects) {
        mValidateActivity = (ValidateActivity) objects[0];
        URL url = (URL) objects[1];

        JsonObject j = null;
        try {
            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
        } catch (IOException e){
            e.printStackTrace();
        }
        /*String jsonString = "{\n" +
                "    \"type\": \"interview_info\",\n" +
                "    \"permission\": \"true\",\n" +
                "    \"info\":{\n" +
                "        \"college_id\": \"01\",\n" +
                "        \"college_name\": \"北清大学\",\n" +
                "        \"site_id\": \"0001\",\n" +
                "        \"site_name\": \"文史楼110\",\n" +
                "        \"periods\":[{\n" +
                "                 \"order\": \"01\",\n" +
                "                \"start_time\": \"2019-06-11 09:00:00\",\n" +
                "                \"end_time\": \"2019-06-11 09:00:00\",\n" +
                "                \"teacher\":[\n" +
                "                    {\n" +
                "                        \"id\":\"11990001\",\n" +
                "                        \"name\": \"何炬\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"student\":[\n" +
                "                    {\n" +
                "                        \"id\":\"11990001\",\n" +
                "                        \"name\": \"宋煦\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"order\": \"02\",\n" +
                "                \"start_time\": \"2019-06-12 09:00:00\",\n" +
                "                \"end_time\": \"2019-06-12 09:00:00\",\n" +
                "                \"teacher\":[\n" +
                "                    {\n" +
                "                        \"id\":\"11990001\",\n" +
                "                        \"name\": \"何炬\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"student\":[\n" +
                "                    {\n" +
                "                        \"id\":\"11990001\",\n" +
                "                        \"name\": \"宋煦\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JsonObject j = new JsonParser().parse(jsonString).getAsJsonObject();*/
        return j;
    }

    @Override
    protected void onPostExecute(JsonObject j) {
        if (j == null
                || j.get("type").isJsonNull()
                || j.get("permission").getAsString().equals("false")){
            // something is wrong
            mValidateActivity.onHttpResponse(false);
        }
        j = j.get("info").getAsJsonObject();
        Interview.getInstance().setInterviewInfo(new InterviewInfo(j));
        Interview.getInstance().setValidated();
        mValidateActivity.onHttpResponse(true);
    }
}
