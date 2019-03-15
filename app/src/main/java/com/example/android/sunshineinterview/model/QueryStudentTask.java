package com.example.android.sunshineinterview.model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.teacheractivities.WaitForStudentSigninActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.URL;

public class QueryStudentTask extends AsyncTask<Object, Void, JsonArray> {
    private final static String TAG = "QueryStudentTask";
    private WaitForStudentSigninActivity mWaitForStudentSignActivity;

    @Override
    protected JsonArray doInBackground(Object... objects) {
        mWaitForStudentSignActivity = (WaitForStudentSigninActivity) objects[0];
        URL url = (URL) objects[1];
        /*JsonObject j = null;
        try{
            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
        } catch (IOException e){
            Log.e(TAG, "Server is not accessible.");
            e.printStackTrace();
        }*/

        String jsonString = "{\n" +
                "    \"type\": \"signin_info\",\n" +
                "    \"info\":[\n" +
                "        {\n" +
                "            \"id\":\"11990001\",\n" +
                "            \"name\": \"何炬\",\n" +
                "            \"is_absent\": \"false\",\n" +
                "            \"img_url\": \"http://10.0.0.1/img/11990001.jpg\" \n" +
                "        },\n" +
                "        {\n" +
                "            \"id\":\"11990002\",\n" +
                "            \"name\": \"宋煦\",\n" +
                "            \"is_absent\": \"true\",\n" +
                "            \"img_url\": \"\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JsonArray j = new JsonParser().parse(jsonString).getAsJsonObject().get("info").getAsJsonArray();
        return j;
    }

    @Override
    protected void onPostExecute(JsonArray j) {
        SigninInfo s = new SigninInfo(j);
        int size = s.students.size();
        String [] names = new String [size];
        String [] imgUrl = new String [size];
        for (int i = 0; i < size; i++){
            names[i] = s.students.get(i).name;
            if (s.students.get(i).isAbsent == false){
                imgUrl[i] = s.students.get(i).imgUrl;
            } else {
                imgUrl[i] = null;
            }
        }
        mWaitForStudentSignActivity.onStudentsUpdate(names, imgUrl);
    }
}