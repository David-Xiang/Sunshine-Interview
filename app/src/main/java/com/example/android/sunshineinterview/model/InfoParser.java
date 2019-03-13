package com.example.android.sunshineinterview.model;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InfoParser {
    private static final String TAG = "InfoParser";

    public static boolean parsePermission(JsonElement j){
        JsonObject e = j.getAsJsonObject();
        String type = e.get("type").getAsString();
        if (!type.equals("permission")){
            Log.e(TAG, "parsePermission: Json Information's type is not Permission.");
            return false;
        }
        return Boolean.valueOf(e.get("permission").getAsString());
    }

    public static InterviewInfo parseInterviewInfo(JsonElement j){
        JsonObject e = j.getAsJsonObject();
        String type = e.get("type").getAsString();
        if (!type.equals("interview_info")){
            Log.e(TAG, "parseInterviewInfo: Json Information's type is not InterviewInfo.");
            return null;
        }

        boolean permission = Boolean.valueOf(e.get("permission").getAsString());
        if (!permission){
            Log.v(TAG, "parseInterviewInfo: Permission denied");
            return null;
        }

        return new InterviewInfo(e.get("info").getAsJsonObject());
    }

    public static SigninInfo parseSigninInfo(JsonElement j){
        JsonObject e = j.getAsJsonObject();
        String type = e.get("type").getAsString();
        if (!type.equals("signin_info")){
            Log.e(TAG, "parseSigninInfo: Json Information's type is not SigninInfo.");
            return null;
        }
        return new SigninInfo(e.get("info").getAsJsonArray());
    }

    public static String parseOrder(JsonElement j){
        JsonObject e = j.getAsJsonObject();
        String type = e.get("type").getAsString();
        if (!type.equals("site_info")){
            Log.e(TAG, "parseOrder: Json Information's type is not SiteInfo.");
            return null;
        }

        boolean permission = Boolean.valueOf(e.get("permission").getAsString());
        if (!permission){
            Log.v(TAG, "parseOrder: Permission denied");
            return null;
        }
        return e.get("info").getAsJsonObject().get("order").getAsString();
    }
}
