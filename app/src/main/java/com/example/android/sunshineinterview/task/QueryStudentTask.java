package com.example.android.sunshineinterview.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.sunshineinterview.model.Interview;
import com.example.android.sunshineinterview.teacheractivities.WaitForStudentSigninActivity;
import com.example.android.sunshineinterview.utilities.NetworkUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.android.sunshineinterview.model.SigninInfo;
import com.example.android.sunshineinterview.utilities.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class QueryStudentTask extends AsyncTask<Object, Void, JsonArray> {
    private final static String TAG = "QueryStudentTask";
    private WaitForStudentSigninActivity mWaitForStudentSignActivity;

    @Override
    protected JsonArray doInBackground(Object... objects) {
        mWaitForStudentSignActivity = (WaitForStudentSigninActivity) objects[0];
        URL url = (URL) objects[1];
        JsonObject j = null;
        JsonArray j2 = null;
        try{
            j = NetworkUtils.getJsonReponse(url).getAsJsonObject();
            j2 = new JsonParser().parse(j.toString()).getAsJsonObject().get("info").getAsJsonArray();
        } catch (IOException e){
            Log.e(TAG, "Server is not accessible.");
            e.printStackTrace();
        }
//        String jsonString = "{\n" +
//                "    \"type\": \"signin_info\",\n" +
//                "    \"info\":[\n" +
//                "        {\n" +
//                "            \"id\":\"11990001\",\n" +
//                "            \"name\": \"何炬\",\n" +
//                "            \"is_absent\": \"false\",\n" +
//                "            \"img_url\": \"http://10.0.0.1/img/1 " +
//                "1990001.jpg\" \n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"id\":\"11990002\",\n" +
//                "            \"name\": \"宋煦\",\n" +
//                "            \"is_absent\": \"true\",\n" +
//                "            \"img_url\": \"\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
//        JsonArray j = new JsonParser().parse(jsonString).getAsJsonObject().get("info").getAsJsonArray();
        return j2;
//        return j;
    }

    @Override
    protected void onPostExecute(JsonArray j) {
        SigninInfo s = new SigninInfo(j);
        int size = s.students.size();
        String [] names = new String [size];
        String [] imgUrl = new String [size];
        Interview mInterview;
        mInterview = Interview.getInstance();
        FileUtils fileUtiles = new FileUtils();
        for (int i = 0; i < size; i++) {
            names[i] = s.students.get(i).name;
            if (!s.students.get(i).isAbsent) {
                imgUrl[i] = s.students.get(i).imgUrl;
                if (!mInterview.getAbsent(s.students.get(i).id))
                    continue;
                String path = new String();
                path = s.students.get(i).imgUrl.substring(s.students.get(i).imgUrl.lastIndexOf('/') + 1);
                if (path.equals(mInterview.getimageUrl(s.students.get(i).id)))
                    continue;
                if (fileUtiles.fileIsExists(path))
                    continue;
                mInterview.signin(s.students.get(i).id, s.students.get(i).name);
                new DownloadTask().execute(mWaitForStudentSignActivity, imgUrl[i], names[i]);
            } else {
                imgUrl[i] = null;
            }
        }
    }
}